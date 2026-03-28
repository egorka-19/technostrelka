#!/usr/bin/env python3
"""
Парсер маршрутов с https://visitudmurtia.org/marshruty/
Требования: pip install requests beautifulsoup4

Контент принадлежит АНО «Центр развития туризма УР» (visitudmurtia.org).
Используйте данные с указанием источника и в соответствии с их правилами.
"""
from __future__ import annotations

import json
import re
import sys
import time
from datetime import datetime, timezone
from html import unescape
from urllib.parse import urljoin, urlparse

import requests
from bs4 import BeautifulSoup

BASE = "https://visitudmurtia.org"
LIST_PATH = "/marshruty/"
# Короткий UA: длинный «Chrome/… Safari» даёт на части страниц урезанный HTML (1 карточка).
USER_AGENT = "Mozilla/5.0 (compatible; Technostrelka/1.0) Chrome/120.0"


def absolutize(url: str) -> str:
    if not url:
        return ""
    return urljoin(BASE, url)


def clean_text(s: str) -> str:
    if not s:
        return ""
    s = unescape(s)
    s = re.sub(r"\s+", " ", s)
    return s.strip()


def strip_html_to_text(html: str) -> str:
    soup = BeautifulSoup(html or "", "html.parser")
    for br in soup.find_all("br"):
        br.replace_with("\n")
    return clean_text(soup.get_text("\n", strip=True))


def fetch(session: requests.Session, url: str) -> str:
    r = session.get(url, timeout=60)
    r.raise_for_status()
    r.encoding = r.apparent_encoding or "utf-8"
    return r.text


def collect_listing_urls(session: requests.Session) -> list[dict]:
    seen: set[str] = set()
    items: list[dict] = []
    # У Bitrix бывают «пустые» страницы пагинации и повторы; стоп после серии без новых URL.
    no_progress = 0
    for page in range(1, 60):
        url = f"{BASE}{LIST_PATH}" if page == 1 else f"{BASE}{LIST_PATH}?PAGEN_1={page}"
        html = fetch(session, url)
        soup = BeautifulSoup(html, "html.parser")
        cards = soup.select("a.announcement[href*='/marshruty/avtomarshruty/']")
        page_new = 0
        for a in cards:
            href = a.get("href") or ""
            if not href or href in seen:
                continue
            seen.add(href)
            page_new += 1
            city_el = a.select_one(".announcement__city")
            title_el = a.select_one(".announcement__title")
            teaser_el = a.select_one(".news-preview__text")
            img_el = a.select_one("img.announcement__img")
            city = clean_text(city_el.get_text()) if city_el else ""
            title = clean_text(title_el.get_text()) if title_el else ""
            teaser = clean_text(teaser_el.get_text()) if teaser_el else ""
            list_img = ""
            if img_el:
                list_img = img_el.get("data-src") or img_el.get("src") or ""
            items.append(
                {
                    "path": href,
                    "city": city,
                    "listing_title": title,
                    "listing_teaser": teaser,
                    "listing_image_url": absolutize(list_img),
                }
            )
        if not cards or page_new == 0:
            no_progress += 1
        else:
            no_progress = 0
        if no_progress >= 5:
            break
        time.sleep(0.25)
    return items


def slug_from_path(path: str) -> str:
    path = path.rstrip("/")
    return path.split("/")[-1] or "route"


def scrape_detail(session: requests.Session, meta: dict) -> dict:
    url = absolutize(meta["path"])
    html = fetch(session, url)
    soup = BeautifulSoup(html, "html.parser")
    slug = slug_from_path(meta["path"])

    title_el = soup.select_one(".cover__title")
    title = clean_text(title_el.get_text()) if title_el else meta.get("listing_title") or slug

    cover_el = soup.select_one("img.cover__img")
    cover = ""
    if cover_el:
        cover = cover_el.get("data-src") or cover_el.get("src") or ""
    cover = absolutize(cover)

    meta_desc = soup.find("meta", attrs={"name": "description"})
    meta_text = clean_text(meta_desc.get("content", "")) if meta_desc else ""

    stops = []
    all_images: list[str] = []
    section_texts: list[str] = []

    for sec in soup.select(".path__section"):
        t_el = sec.select_one(".path__title")
        a_el = sec.select_one(".path__address")
        txt_el = sec.select_one(".path__text")
        st_title = clean_text(t_el.get_text()) if t_el else ""
        addr = ""
        if a_el:
            addr = clean_text(a_el.get_text())
            for svg in a_el.select("svg"):
                pass
            addr = re.sub(r"^\s+", "", addr)
        body_html = ""
        if txt_el:
            body_html = str(txt_el)
        stop_images = []
        if txt_el:
            for im in txt_el.find_all("img"):
                src = im.get("src") or ""
                au = absolutize(src)
                if au:
                    stop_images.append(au)
                    all_images.append(au)
        body_text = strip_html_to_text(body_html)
        if not st_title and not body_text and not addr:
            continue
        stops.append(
            {
                "title": st_title,
                "address": addr,
                "text": body_text,
                "image_urls": stop_images,
            }
        )
        if body_text:
            section_texts.append((st_title + "\n" if st_title else "") + body_text)

    if cover:
        all_images.insert(0, cover)

    # Уникальные картинки, порядок сохранён
    uniq_img: list[str] = []
    for u in all_images:
        if u and u not in uniq_img:
            uniq_img.append(u)

    full_text = "\n\n".join(section_texts)
    lead_parts = [p for p in [meta.get("listing_teaser"), meta_text] if p]
    description = "\n\n".join(lead_parts)
    if full_text:
        description = (description + "\n\n" if description else "") + full_text

    place = meta.get("city") or "Удмуртия"
    category = meta.get("city") or "Удмуртия"
    if not category:
        category = "Удмуртия"

    route_id = f"visitudm-{slug}"
    goal = "Гастрономия" if "ельмен" in title.lower() else "Экскурсия"

    return {
        "id": route_id,
        "name": title,
        "slug": slug,
        "category": category,
        "place": place,
        "listing_image_url": meta.get("listing_image_url") or "",
        "cover_image_url": cover,
        "image_urls": uniq_img,
        "description": description.strip(),
        "url": url,
        "goal": goal,
        "days_range": "1",
        "people_count": "",
        "duration": "",
        "difficulty": "",
        "stops": stops,
    }


def log(msg: str) -> None:
    print(msg, file=sys.stderr, flush=True)


def main() -> None:
    session = requests.Session()
    session.headers.update({"User-Agent": USER_AGENT})

    log("Сбор списка карточек…")
    listing = collect_listing_urls(session)
    log(f"Найдено карточек: {len(listing)}")

    routes = []
    for i, meta in enumerate(listing):
        try:
            time.sleep(0.35)
            r = scrape_detail(session, meta)
            routes.append(r)
            log(f"  [{i+1}/{len(listing)}] {r['name'][:60]}…")
        except Exception as e:
            log(f"  ОШИБКА {meta.get('path')}: {e}")

    out = {
        "version": 1,
        "scraped_at": datetime.now(timezone.utc).isoformat(),
        "source": BASE + LIST_PATH,
        "license_note": "Данные с публичного сайта visitudmurtia.org; проверьте условия использования.",
        "routes": routes,
    }
    json.dump(out, sys.stdout, ensure_ascii=False, indent=2)
    sys.stdout.write("\n")


if __name__ == "__main__":
    main()

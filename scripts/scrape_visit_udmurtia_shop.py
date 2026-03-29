#!/usr/bin/env python3
"""
Каталог с https://visitudmurtiashop.ru/ (Tilda). Зависимости: pip install requests beautifulsoup4
"""
from __future__ import annotations

import json
import re
import sys
import time
from datetime import datetime, timezone
from html import unescape

import requests
from bs4 import BeautifulSoup

BASE = "https://visitudmurtiashop.ru"
USER_AGENT = "Mozilla/5.0 (compatible; TechnostrelkaShop/1.0) Chrome/120.0"


def clean(s: str) -> str:
    if not s:
        return ""
    s = unescape(s)
    s = re.sub(r"<br\s*/?>", "\n", s, flags=re.I)
    s = BeautifulSoup(s, "html.parser").get_text("\n", strip=True)
    return re.sub(r"\n{3,}", "\n\n", s).strip()


def guess_shop_category(name: str) -> str:
    n = name.lower()
    if "футболка" in n:
        return "Футболки"
    if "свитшот" in n or "худи" in n or "бомбер" in n:
        return "Худи и свитшоты"
    return "Сувениры"


def fetch(session: requests.Session) -> str:
    r = session.get(BASE + "/", timeout=60)
    r.raise_for_status()
    r.encoding = r.apparent_encoding or "utf-8"
    return r.text


def parse_products(html: str) -> list[dict]:
    soup = BeautifulSoup(html, "html.parser")
    out: list[dict] = []
    seen: set[str] = set()

    for col in soup.select("div.t778__col.js-product"):
        if col.find_parent("div", class_="t-popup"):
            continue
        lid = col.get("data-product-lid")
        if not lid or lid in seen:
            continue
        seen.add(lid)

        name_el = col.select_one(".js-product-name")
        price_el = col.select_one(".js-product-price")
        old_el = col.select_one(".t778__price_old .t778__price-value")
        descr_el = col.select_one(".t778__descr")
        img_el = col.select_one(".js-product-img")

        name = clean(name_el.get_text("\n", strip=True)) if name_el else lid
        descr = clean(descr_el.decode_contents()) if descr_el else ""

        price = None
        if price_el:
            raw = re.sub(r"[^\d]", "", price_el.get_text())
            if raw:
                price = int(raw)
        old_price = None
        if old_el and old_el.get_text(strip=True):
            raw = re.sub(r"[^\d]", "", old_el.get_text())
            if raw:
                old_price = int(raw)

        image_urls: list[str] = []
        if img_el:
            u = (img_el.get("data-original") or "").strip()
            if u.startswith("http"):
                image_urls.append(u)
        for bg in col.select(".t778__bgimg_second[data-original]"):
            u = (bg.get("data-original") or "").strip()
            if u.startswith("http") and u not in image_urls:
                image_urls.append(u)

        popup = soup.find(id=f"t778__product-{lid}")
        if popup:
            for el in popup.select("[data-original]"):
                u = (el.get("data-original") or "").strip()
                if u.startswith("http") and "tildacdn.com" in u and u not in image_urls:
                    image_urls.append(u)

        collection = "Visit Udmurtia Shop"
        prev = col.find_parent("div", class_="r")
        if prev:
            for sib in prev.find_all_previous("div", class_="t795__title", limit=5):
                t = clean(sib.get_text(" ", strip=True))
                if t and len(t) > 3:
                    collection = t.replace("Коллекция", "").strip()
                    break

        out.append(
            {
                "id": f"vus-{lid}",
                "lid": lid,
                "collection": collection,
                "shop_category": guess_shop_category(name),
                "name": name,
                "description": descr,
                "price_rub": price,
                "price_old_rub": old_price,
                "place": "Ижевск",
                "image_urls": image_urls,
                "product_url": f"{BASE}/",
            }
        )

    return out


def main() -> None:
    session = requests.Session()
    session.headers.update({"User-Agent": USER_AGENT})
    log = lambda m: print(m, file=sys.stderr, flush=True)
    log("Загрузка страницы…")
    html = fetch(session)
    time.sleep(0.3)
    products = parse_products(html)
    log(f"Товаров: {len(products)}")
    doc = {
        "version": 1,
        "scraped_at": datetime.now(timezone.utc).isoformat(),
        "source": BASE,
        "license_note": "Данные с visitudmurtiashop.ru; уточните права на коммерческое использование.",
        "products": products,
    }
    json.dump(doc, sys.stdout, ensure_ascii=False, indent=2)
    sys.stdout.write("\n")


if __name__ == "__main__":
    main()

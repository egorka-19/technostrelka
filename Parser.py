import requests
from datetime import datetime
import openpyxl

# URL API
url = 'https://kudago.com/public-api/v1.4/events/'

# Временной интервал (сейчас + неделя вперёд)
now = int(datetime.now().timestamp())
one_week_later = now + 7 * 24 * 60 * 60

# Параметры запроса
params = {
    'location': 'nnv',
    'lang': 'ru',
    'fields': 'title,dates,place,description,site_url,age_restriction',
    'expand': 'place',
    'actual_since': now,
    'actual_until': one_week_later,
    'page_size': 100
}

# Запрос к API
response = requests.get(url, params=params)

if response.status_code == 200:
    data = response.json()

    # 📘 events.xlsx — события с датой
    wb_events = openpyxl.Workbook()
    sheet_events = wb_events.active
    sheet_events.title = "События"
    sheet_events.append(["Название", "Место", "Дата начала", "Ссылка", "Возрастное ограничение"])

    # 📘 places.xlsx — события без даты
    wb_places = openpyxl.Workbook()
    sheet_places = wb_places.active
    sheet_places.title = "Без даты"
    sheet_places.append(["Название", "Место", "Ссылка", "Возрастное ограничение"])

    for event in data['results']:
        title = event.get('title', 'Без названия')

        place = event.get('place')
        place_name = place.get('title') if place else "Не указано"

        site_url = event.get('site_url', 'Не указано')

        age = event.get('age_restriction')
        age_str = f"{age}" if age else "0+"

        # Проверка даты
        start_date = None
        valid_date = True
        dates = event.get('dates')

        if dates and 'start' in dates[0]:
            start_timestamp = dates[0]['start']
            if start_timestamp:
                if start_timestamp > 10**10:
                    start_timestamp //= 1000
                try:
                    start_date = datetime.fromtimestamp(start_timestamp).strftime('%d.%m.%Y %H:%M')
                except:
                    valid_date = False
            else:
                valid_date = False
        else:
            valid_date = False

        if valid_date:
            # Добавляем в events.xlsx
            sheet_events.append([title, place_name, start_date, site_url, age_str])
        else:
            # Добавляем в places.xlsx (без даты)
            sheet_places.append([title, place_name, site_url, age_str])

    # Сохраняем оба файла
    wb_events.save("events.xlsx")
    wb_places.save("places.xlsx")

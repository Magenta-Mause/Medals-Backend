"""
Fetches discipline and performance requirement data from the official Sportabzeichen API,
and converts it into a CSV format suitable for updating our backend system.

This script helps track changes in performance metrics or disciplines, especially for new years
(e.g., when updated requirements become valid).

Usage:
- Run manually as needed to generate an up-to-date disciplines_output.csv.
- Make sure cookies and headers are valid.

Notes:
- Ignores unofficial or irrelevant disciplines listed in IGNORE_LIST.
- Default year is set to 2025; adjust VALID_YEAR constant if necessary or fetch it dynamically.
"""

import csv
import requests
from pprint import pprint


# Constants
BASE_URL = "https://sportabzeichen-digital.de/api/v1/disciplines"
OUTPUT_FILE = "disciplines_output.csv"
VALID_YEAR = "2025"

HEADERS = {
    'accept': 'application/json',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36',
    'referer': 'https://sportabzeichen-digital.de/requirements',
}

COOKIES = {
    "XSRF-TOKEN": "<your-xsrf-token-here>",
    "sportabzeichen_digital_session": "<your-session-token-here>",
}

IGNORE_LIST = {"gerätturnen", "verbandsabzeichen"}

GENDERS = {1: "Male", 2: "Female"}
AGE_GROUPS = [1, 2, 3, 4, 5, 6]

CSV_FIELDS = [
    "DISCIPLINE_ID", "NAME", "VALID_IN", "START_AGE", "END_AGE",
    "BRONZE_FEMALE", "SILVER_FEMALE", "GOLD_FEMALE",
    "BRONZE_MALE", "SILVER_MALE", "GOLD_MALE"
]

AGE_GROUP_MAPPING = {
    1: (6, 7),
    2: (8, 9),
    3: (10, 11),
    4: (12, 13),
    5: (14, 15),
    6: (16, 17),
}


def age_group_to_start_end(age_group_id: int) -> tuple[int, int]:
    return AGE_GROUP_MAPPING.get(age_group_id, (None, None))


def fetch_disciplines(gender_id: int, age_group_id: int) -> list:
    params = {
        "f[group_id][eq]": 4,
        "with_conditions": 0,
        "resource": "requirements",
        "f[disability_class_id][null]": "",
        "f[gender_id][eq]": gender_id,
        "f[age_group_id][eq]": age_group_id,
        "f[performance_condition][nnull]": ""
    }
    print(f"Fetching disciplines for gender={gender_id} age_group={age_group_id}...")
    response = requests.get(BASE_URL, headers=HEADERS, params=params, cookies=COOKIES)
    
    if response.ok:
        return response.json().get("data", [])
    else:
        print(f"Warning: Failed to fetch (status {response.status_code})")
        return []


def update_or_create_row(rows: list, discipline_data: dict, start_age: int, end_age: int):
    discipline_id = discipline_data["id"]
    name = discipline_data["name"]

    if name.lower() in IGNORE_LIST:
        print(f"Ignoring discipline: {name}")
        return

    existing_row = next(
        (r for r in rows if r["DISCIPLINE_ID"] == discipline_id and r["START_AGE"] == start_age and r["END_AGE"] == end_age),
        None
    )

    if not existing_row:
        existing_row = {
            "DISCIPLINE_ID": discipline_id,
            "NAME": name,
            "VALID_IN": VALID_YEAR,
            "START_AGE": start_age,
            "END_AGE": end_age,
            "BRONZE_FEMALE": '',
            "SILVER_FEMALE": '',
            "GOLD_FEMALE": '',
            "BRONZE_MALE": '',
            "SILVER_MALE": '',
            "GOLD_MALE": '',
        }
        rows.append(existing_row)

    for perf in discipline_data.get("performance_conditions", []):
        perf_data = perf["data"]
        gender = perf_data["gender_id"]
        medal = perf_data["performance_area"]["label"]
        value = perf_data["performance_condition_readable"]

        if gender == 1:  # Male
            if medal == "Bronze":
                existing_row["BRONZE_MALE"] = value
            elif medal == "Silber":
                existing_row["SILVER_MALE"] = value
            elif medal == "Gold":
                existing_row["GOLD_MALE"] = value
        elif gender == 2:  # Female
            if medal == "Bronze":
                existing_row["BRONZE_FEMALE"] = value
            elif medal == "Silber":
                existing_row["SILVER_FEMALE"] = value
            elif medal == "Gold":
                existing_row["GOLD_FEMALE"] = value


def save_to_csv(rows: list, filename: str):
    rows.sort(key=lambda r: (r["DISCIPLINE_ID"], r["START_AGE"]))
    with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=CSV_FIELDS)
        writer.writeheader()
        writer.writerows(rows)
    print(f"Done. Saved to {filename}")


def main():
    rows = []

    for gender_id in GENDERS:
        for age_group_id in AGE_GROUPS:
            disciplines = fetch_disciplines(gender_id, age_group_id)
            start_age, end_age = age_group_to_start_end(age_group_id)

            for entry in disciplines:
                discipline = entry["data"]
                update_or_create_row(rows, discipline, start_age, end_age)

    save_to_csv(rows, OUTPUT_FILE)


if __name__ == "__main__":
    main()


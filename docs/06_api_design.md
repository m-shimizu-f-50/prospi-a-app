# 06. API設計

- 作成日: 2026-08-07
- 位置づけ: [04_screen_design.md](./04_screen_design.md)(画面設計)・[05_database_design.md](./05_database_design.md)(DB設計)を踏まえたREST APIの設計。
- 対象: MVP(P0)のエンドポイントを中心に、P1で追加するエンドポイントも軽く設計する。

---

## 1. 設計方針

- **RESTfulに統一する**: 現行の `/players/create` のような動詞入りパスを廃止し、リソース名+HTTPメソッドで表現する(CLAUDE.mdの方針に準拠)。
- **JSONの命名規則はcamelCaseに統一する**: Spring Boot(Jackson)のデフォルト規約に合わせる。これにより、現行フロントの`axios-case-converter`(snake↔camel変換ミドルウェア)は**廃止**する。DBカラム(snake_case)とAPIレスポンス(camelCase)の変換はSpring Boot側(Jacksonのデフォルト動作)に任せる。
- **ベースパス**: `/api`(バージョニングは行わない。個人利用アプリのため、必要になった時点で検討する)
- **対戦記録のネストリソースとして選手成績を扱う**: `record_player_stats` は単独のURLを持たず、`/api/records/{id}/player-stats` という対戦記録配下のリソースとして表現する(DB設計の「箱(records)+中身(record_player_stats)」という構造をAPIにもそのまま反映する)。

## 2. 共通仕様

### ステータスコード
| コード | 用途 |
|--------|------|
| 200 OK | 取得・更新成功 |
| 201 Created | 新規作成成功 |
| 204 No Content | 削除成功 |
| 400 Bad Request | リクエスト形式が不正 |
| 404 Not Found | 対象が存在しない |
| 422 Unprocessable Entity | バリデーションエラー |
| 500 Internal Server Error | サーバー内部エラー |

### エラーレスポンスの形式(共通)
```json
{
  "message": "入力内容に誤りがあります",
  "errors": {
    "name": ["名前は必須です"]
  }
}
```
`errors`はバリデーションエラー時のみ付与する。

## 3. エンドポイント一覧(MVP)

### 3.1 選手(players)

| Method | Path | 概要 |
|--------|------|------|
| GET | `/api/players` | 選手一覧取得。`?type=batter`or`pitcher`でフィルタ可能 |
| POST | `/api/players` | 選手を新規登録 |
| GET | `/api/players/{id}` | 選手詳細取得 |
| PUT | `/api/players/{id}` | 選手情報を更新 |
| DELETE | `/api/players/{id}` | 選手を削除 |

**現行からの変更点**: `POST /players/create` → `POST /players` に統一(RESTful化)。

### 3.2 対戦記録(records)

| Method | Path | 概要 |
|--------|------|------|
| GET | `/api/records` | 対戦記録一覧取得。`?type=rank_battle`/`room_battle`/`tournament`でフィルタ可能 |
| POST | `/api/records` | 対戦記録を新規作成(「箱」を作る) |
| GET | `/api/records/{id}` | 対戦記録詳細取得(紐づく選手成績を含む) |
| PUT | `/api/records/{id}` | 対戦記録を更新(`name`, `win`, `lose`) |
| DELETE | `/api/records/{id}` | 対戦記録を削除(紐づく`record_player_stats`もCASCADE削除) |

### 3.3 対戦記録ごとの選手成績(records配下のネストリソース)

| Method | Path | 概要 |
|--------|------|------|
| PUT | `/api/records/{id}/player-stats` | 選手成績をまとめてupsert(登録済みならUPDATE、未登録ならINSERT) |
| DELETE | `/api/records/{id}/player-stats/{playerId}` | 特定選手の成績行を削除(その対戦記録の出場から外す) |

**PUTの挙動に関する注記**: このPUTは「送信された`playerId`の行だけ」をupsertする(送信されなかった既存行は変更しない)。対戦記録全体を丸ごと置き換える厳密なREST的PUTではなく、実用上の「選手ごとの差分upsert」として設計している。特定選手を出場から外したい場合は、個別に`DELETE .../player-stats/{playerId}`を呼ぶ。

## 4. リクエスト/レスポンス例

### 4.1 選手登録: `POST /api/players`

リクエスト:
```json
{
  "name": "イチロー",
  "position": "CF",
  "series": "24ドリームオーダー",
  "type": "batter",
  "spirit": 4500,
  "limitBreak": 4,
  "average": 0.325,
  "trajectory": "6",
  "meet": 90,
  "power": 85,
  "speed": 88,
  "skill1": 3,
  "skill2": 14,
  "skill3": null
}
```

レスポンス(201):
```json
{
  "id": 1,
  "name": "イチロー",
  "position": "CF",
  "series": "24ドリームオーダー",
  "type": "batter",
  "spirit": 4500,
  "limitBreak": 4,
  "average": 0.325,
  "trajectory": "6",
  "meet": 90,
  "power": 85,
  "speed": 88,
  "era": null,
  "velocity": null,
  "control": null,
  "stamina": null,
  "skill1": 3,
  "skill2": 14,
  "skill3": null,
  "createdAt": "2026-08-07T10:00:00",
  "updatedAt": "2026-08-07T10:00:00"
}
```

### 4.2 対戦記録の作成: `POST /api/records`

ルーム戦・大会の場合(名前あり):
```json
{ "type": "room_battle", "name": "8月度ルーム戦" }
```

ランク戦の場合(名前は省略可):
```json
{ "type": "rank_battle" }
```

レスポンス(201):
```json
{
  "id": 201,
  "type": "room_battle",
  "name": "8月度ルーム戦",
  "win": null,
  "lose": null,
  "createdAt": "2026-08-07T10:00:00",
  "updatedAt": "2026-08-07T10:00:00",
  "playerStats": []
}
```

### 4.3 対戦記録詳細: `GET /api/records/{id}`

```json
{
  "id": 201,
  "type": "room_battle",
  "name": "8月度ルーム戦",
  "win": 5,
  "lose": 2,
  "createdAt": "2026-08-07T10:00:00",
  "updatedAt": "2026-08-07T12:30:00",
  "playerStats": [
    {
      "player": { "id": 1, "name": "イチロー", "type": "batter", "position": "CF" },
      "order": 1,
      "atBats": 20,
      "hits": 8,
      "doubles": 2,
      "triples": 0,
      "homeRuns": 1,
      "rbi": 5,
      "wins": null,
      "losses": null,
      "saves": null
    },
    {
      "player": { "id": 2, "name": "山本", "type": "pitcher", "position": "SP" },
      "order": null,
      "atBats": null,
      "hits": null,
      "doubles": null,
      "triples": null,
      "homeRuns": null,
      "rbi": null,
      "wins": 3,
      "losses": 1,
      "saves": 0
    }
  ]
}
```

### 4.4 選手成績のまとめてupsert: `PUT /api/records/{id}/player-stats`

リクエスト:
```json
{
  "playerStats": [
    { "playerId": 1, "order": 1, "atBats": 20, "hits": 8, "doubles": 2, "triples": 0, "homeRuns": 1, "rbi": 5 },
    { "playerId": 2, "order": null, "wins": 3, "losses": 1, "saves": 0 }
  ]
}
```

レスポンス(200): `GET /api/records/{id}` と同じ形式(更新後の対戦記録詳細を返す)。

## 5. エンドポイント一覧(P1)

P1(分析・効率化機能)で追加するエンドポイント。詳細は実装フェーズで改めて設計する。

| Method | Path | 概要 |
|--------|------|------|
| GET | `/api/players/{id}/records` | 特定選手の全出場記録を取得(成績推移グラフ用) |
| GET | `/api/players/rankings?stat={statName}&type={recordType}` | 指定した成績項目でのランキング取得 |
| GET | `/api/order-templates` | オーダーテンプレート一覧取得 |
| POST | `/api/order-templates` | オーダーテンプレート新規作成 |
| GET | `/api/order-templates/{id}` | オーダーテンプレート詳細取得 |
| PUT | `/api/order-templates/{id}` | オーダーテンプレート更新 |
| DELETE | `/api/order-templates/{id}` | オーダーテンプレート削除 |
| POST | `/api/records/{id}/attachments` | スクリーンショット画像をアップロード(multipart/form-data) |
| DELETE | `/api/records/{id}/attachments/{attachmentId}` | 添付画像を削除 |

**選手比較機能について**: 専用エンドポイントは設けず、`GET /api/players/{id}/records` を比較対象の選手ぶん呼び出し、フロント側で並べて表示する想定(不要な専用APIを増やさない)。

## 6. 未決定事項

- `GET /api/players/rankings` の集計をDB(SQLの集約関数)で行うか、アプリ層で行うかは実装フェーズで検討する
- 添付ファイル(`POST /api/records/{id}/attachments`)の保存先(ローカルストレージ/S3等)は [07_architecture.md](./07_architecture.md)(作成予定)で決定する
- ページネーション・ソートのクエリパラメータ仕様(選手数・対戦記録数が増えた場合に必要になる可能性)
- バリデーションルールの詳細(各項目の最大値・必須/任意の最終確認)は実装時にコントローラー単位で詰める

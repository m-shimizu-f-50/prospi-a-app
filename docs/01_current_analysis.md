# 01. 現行アプリ分析

- 解析対象: `/Users/shimizumasato/Desktop/dev/nextjs/prospi-orix-tools`
- 解析日: 2026-08-05
- 方針: コード変更は行わず、静的解析のみ実施。推測で補完せず、根拠が確認できない項目は「不明」と記載する。

---

## 1. アプリ概要

プロスピA(スマートフォン向け野球ゲーム)における「オリックス純正チーム」運用を支援するための管理ツール。
選手データ(スピリッツ・特能・能力値等)の登録・一覧管理と、大会(ランク戦/ルーム戦/カップ戦/リーグ戦)ごとの選手成績・オーダー(打順、投手起用)の記録を行うWebアプリケーション。

- Header.tsx のナビゲーション構成(トップ / 選手一覧 / ランク・ルーム戦 / 大会)から、上記4領域がアプリの中心機能であると判断。

## 2. 目的

明確な目的・背景を記載したドキュメント(README、企画書等)は現行リポジトリ内に見つからなかった。
frontend/README.md は `create-next-app` のデフォルトテンプレートのままであり、プロジェクト固有の目的記述はない。

- **不明**(現行リポジトリ内に目的を明記した一次情報なし。ユーザーへのヒアリングが必要)

## 3. 機能一覧

コード(コントローラー・ページ・APIクライアント)から読み取れる機能を、実装状況とあわせて記載する。

| # | 機能 | 実装状況 | 根拠 |
|---|------|----------|------|
| 1 | 選手一覧取得 | 実装済み | `PlayerController@index`, `GET /api/players` |
| 2 | 選手新規登録 | 実装済み(登録時に全大会分の初期成績レコードを自動作成) | `PlayerController@create`, `POST /api/players/create` |
| 3 | 選手詳細取得 | **未実装**(空メソッド) | `PlayerController@show` |
| 4 | 選手更新 | **未実装**(空メソッド)。フロント側 `updatePlayer` も未定義変数参照でエラーになる状態 | `PlayerController@update`, `frontend/src/app/api/players.ts` |
| 5 | 選手削除 | **未実装**(空メソッド)。フロント側 `deletePlayer` も同様に未定義変数参照 | `PlayerController@destroy`, `frontend/src/app/api/players.ts` |
| 6 | 大会一覧取得(種別フィルタ可) | 実装済み | `TournamentController@index`, `GET /api/tournaments` |
| 7 | 大会新規作成 | コントローラーは実装済みだが、バリデーション項目(`start_date`必須等)が現行DBスキーマと不整合(後述)のため**実行時エラーになる可能性が高い** | `TournamentController@store` |
| 8 | 大会詳細取得(選手+成績込み) | 実装済み | `TournamentController@details`, `GET /api/tournaments/{id}/details` |
| 9 | 大会更新・削除 | コントローラーは実装済みだが#7と同様のスキーマ不整合あり | `TournamentController@update/destroy` |
| 10 | 選手成績の一括更新(打者・投手) | コントローラーは実装済みだが、**存在しないカラム**(`games`, `draws`, `win_rate`, `innings`, `strikeouts`, `whip`, `average`等)へ書き込もうとしており、実行時にSQLエラーになる可能性が高い | `TournamentController@bulkUpdatePlayerStats` |
| 11 | 大会ごとの選手成績個別取得/更新/削除、成績統計 | **未実装**。ルート定義はあるが `PlayerStatsController` 自体がファイルとして存在せず、クラス未検出エラーになる | `routes/api.php` の `PlayerStatsController` 参照箇所 |
| 12 | ランク戦/ルーム戦のオーダー管理(打順・投手起用) | フロント実装あり(`rank/page.tsx`, 1064行) | `frontend/src/app/rank/page.tsx` |
| 13 | トースト通知(操作結果のフィードバック) | 実装済み | `contexts/ToastContext.tsx`, `TOAST_IMPLEMENTATION.md` |

## 4. 画面一覧

Header.tsx のナビゲーションおよび `src/app` 配下のルーティングから判断。

| 画面 | パス | ファイル | 概要・状態 |
|------|------|----------|-----------|
| トップページ | `/` | `src/app/page.tsx` | 見出しのみのプレースホルダー(未実装に近い) |
| 選手一覧 | `/players` | `src/app/players/page.tsx` | 32行のみ。中身は簡素(詳細は要確認) |
| 選手登録 | `/players/register` | `src/app/players/register/page.tsx` | `PlayerForm` コンポーネントを表示 |
| ランク/ルーム戦 | `/rank` | `src/app/rank/page.tsx` | 1064行の大規模ページ。オーダー編成・成績入力UIを含む |
| 大会 | `/tournaments` | `src/app/tournaments/page.tsx` | 890行の大規模ページ。大会作成モーダル・成績一括更新UIを含む |

選手編集・削除・選手詳細の専用画面は見当たらない(APIも未実装のため画面側も対応していない可能性が高い)。

## 5. ディレクトリ構成

```
prospi-orix-tools/
├── backend/                      # Laravel 10 (PHP 8.1)
│   ├── app/
│   │   ├── Http/Controllers/
│   │   │   ├── Controller.php
│   │   │   ├── PlayerController.php
│   │   │   └── TournamentController.php   # PlayerStatsController は不在(routesのみ参照)
│   │   └── Models/
│   │       ├── Player.php
│   │       ├── PlayerStat.php             # PlayerStats と重複気味のモデル(後述)
│   │       ├── PlayerStats.php
│   │       ├── Tournament.php
│   │       └── User.php
│   ├── database/
│   │   ├── migrations/           # 11ファイル(下記DB構成参照)
│   │   └── seeders/              # DatabaseSeeder, TournamentSeeder
│   └── routes/api.php
└── frontend/                     # Next.js 14 (App Router) / React 18 / TypeScript
    └── src/app/
        ├── api/                  # players.ts, tournaments.ts (axiosラッパー)
        ├── components/
        │   ├── Header.tsx, Footer.tsx
        │   └── player/PlayerForm.tsx, PlayerTable.tsx
        ├── constants/common.ts   # ポジション・特能・弾道等のマスタ定数
        ├── contexts/ToastContext.tsx
        ├── hooks/usePlayerData.ts, useTournamentData.ts
        ├── lib/axios.ts          # axios-case-converter によるsnake↔camel変換
        ├── providers/QueryProvider.tsx  # TanStack Query
        ├── types/player.ts, tournament.ts
        ├── players/(page.tsx, register/page.tsx)
        ├── rank/page.tsx
        ├── tournaments/page.tsx
        └── page.tsx
```

## 6. DB構成

MySQL。マイグレーション履歴から復元した現行スキーマ(実際にマイグレーションを適用済みかは未確認)。

### players
| カラム | 型 | 備考 |
|--------|----|----|
| id | bigint (PK) | |
| name | string | |
| position | string | |
| series | string, nullable | 後から追加(`add_series_to_players_table`) |
| type | enum('batter','pitcher') | |
| spirit | integer | スピリッツ値 |
| limit_break | tinyint | 限界突破段階 |
| average | decimal(5,3), nullable | 打者用 |
| trajectory | string, nullable | 打者用(弾道) |
| meet | integer, nullable | 打者用 |
| power | integer, nullable | 打者用 |
| speed | integer, nullable | 打者用 |
| era | decimal(4,2), nullable | 投手用 |
| velocity | integer, nullable | 投手用 |
| control | integer, nullable | 投手用 |
| stamina | integer, nullable | 投手用 |
| skill1 / skill2 / skill3 | string, nullable | 特能(後から追加) |
| created_at / updated_at | timestamp | |

### tournaments
| カラム | 型 | 備考 |
|--------|----|----|
| id | bigint (PK) | |
| name | string | |
| slug | string, unique | |
| type | string, default 'tournament' | コメント上は `'rank' or 'tournament'` だが、`Tournament`モデルの`getValidTypes()`は`rank_battle/cup/league`を返しており**値の定義が二重管理かつ不整合** |
| win / lose | unsignedInteger, default 0 | |
| ~~start_date / end_date~~ | (削除済み) | `2025_07_31_131302` マイグレーションで削除。しかし `Tournament` モデルの `$fillable`・`$casts` およびコントローラーのバリデーションには残存(スキーマとコードの不整合) |
| created_at / updated_at | timestamp | |

`description` カラムは**マイグレーションに存在しない**が、`Tournament`モデルの`$fillable`とコントローラーのバリデーションには含まれている(不整合)。

### player_stats
| カラム | 型 | 備考 |
|--------|----|----|
| id | bigint (PK) | |
| player_id | FK → players.id (cascade delete) | |
| tournament_id | FK → tournaments.id (cascade delete) | |
| position_type | enum('batter','pitcher') | |
| order | unsignedTinyInteger, nullable | 打順/起用順 |
| is_bench | boolean, default false | |
| at_bats / hits / doubles / triples / home_runs / rbi | unsignedInteger, nullable | 打者成績 |
| wins / losses / saves | unsignedInteger, nullable | 投手成績 |
| created_at / updated_at | timestamp | |

`TournamentController@bulkUpdatePlayerStats` は上記に存在しない `games`, `draws`, `win_rate`, `innings`, `strikeouts`, `whip`, `average` へ書き込もうとしており、**実行時にSQLエラーとなる可能性が高い**(未検証・コード読解による推定)。

### users / password_reset_tokens / failed_jobs / personal_access_tokens
Laravel標準の認証・キュー機構用テーブル(Sanctum導入済みだが、`routes/api.php`で`auth:sanctum`ミドルウェアを使うのは`/api/user`のみで、実質的に認証は未使用と見られる)。

- 認証機能が実際に画面から使われているかは**不明**(フロントにログイン画面が見当たらない)。

## 7. API一覧

`routes/api.php` に定義されているエンドポイント。実装状況は6.で述べた通り一部が壊れている。

| Method | Path | Controller@Action | 実装状況 |
|--------|------|--------------------|----------|
| GET | /api/user | Closure (`auth:sanctum`) | 実装済み(用途不明) |
| GET | /api/players | PlayerController@index | 実装済み |
| POST | /api/players/create | PlayerController@create | 実装済み |
| GET | /api/players/{id} | PlayerController@show | 未実装(空) |
| PUT | /api/players/{id} | PlayerController@update | 未実装(空) |
| DELETE | /api/players/{id} | PlayerController@destroy | 未実装(空) |
| GET | /api/tournaments | TournamentController@index | 実装済み |
| POST | /api/tournaments | TournamentController@store | スキーマ不整合あり |
| GET | /api/tournaments/{id} | TournamentController@show | 実装済み |
| PUT | /api/tournaments/{id} | TournamentController@update | スキーマ不整合あり |
| DELETE | /api/tournaments/{id} | TournamentController@destroy | 実装済み |
| GET | /api/tournaments/{id}/details | TournamentController@details | 実装済み |
| POST | /api/tournaments/{id}/player-stats/bulk-update | TournamentController@bulkUpdatePlayerStats | 存在しないカラムへの書き込みあり |
| GET | /api/tournaments/{tournamentId}/player-stats | PlayerStatsController@index | **コントローラー未実装**(ファイル不在) |
| GET | /api/tournaments/{tournamentId}/statistics | PlayerStatsController@statistics | **コントローラー未実装**(ファイル不在) |
| GET | /api/tournaments/{tournamentId}/players/{playerId}/stats | PlayerStatsController@show | **コントローラー未実装**(ファイル不在) |
| PUT | /api/tournaments/{tournamentId}/players/{playerId}/stats | PlayerStatsController@update | **コントローラー未実装**(ファイル不在) |
| DELETE | /api/tournaments/{tournamentId}/players/{playerId}/stats | PlayerStatsController@destroy | **コントローラー未実装**(ファイル不在) |

APIはRESTfulな設計を意図しているが、`/players/create`(動詞を含むパス)は厳密なREST規約からは逸脱している。

## 8. 主要ライブラリ

### フロントエンド (`frontend/package.json`)
| ライブラリ | バージョン | 用途 |
|-----------|-----------|------|
| next | 14.2.30 | フレームワーク(App Router) |
| react / react-dom | ^18 | UI |
| typescript | ^5 | 型 |
| @mui/material, @mui/icons-material, @emotion/* | ^7.2.0 / ^11.14.0 | UIコンポーネント |
| @tanstack/react-query, react-query-devtools | ^5.83.0 | サーバー状態管理・キャッシュ |
| axios-case-converter | ^1.1.1 | snake_case ⇔ camelCase 自動変換(axios自体は依存に明記されていないが実質必須) |
| tailwindcss | ^3.4.1 | CSS(MUIと併用) |

### バックエンド (`backend/composer.json`)
| ライブラリ | バージョン | 用途 |
|-----------|-----------|------|
| php | ^8.1 | |
| laravel/framework | ^10.10 | フレームワーク |
| laravel/sanctum | ^3.3 | API認証(現状ほぼ未使用と見られる) |
| laravel/tinker | ^2.8 | REPL |
| guzzlehttp/guzzle | ^7.2 | HTTPクライアント |

DB: MySQL(`.env.example`より `DB_CONNECTION=mysql`)。

## 9. 改善点

現行コードを静的に読んだ範囲で確認できた、設計・実装上の問題点。

### 致命的(リプレイスで必ず解消すべき)
1. **モデル/バリデーションとDBスキーマの不整合**
   - `Tournament` モデルと `TournamentController` が、マイグレーションで削除済みの `start_date` / `end_date` を必須項目としてバリデーションしており、大会の新規作成・更新が失敗する可能性が高い。
   - 同様に存在しない `description` カラムを保存しようとしている。
   - `bulkUpdatePlayerStats` が `player_stats` テーブルに存在しない列(`games`, `draws`, `win_rate`, `innings`, `strikeouts`, `whip`, `average`)へ書き込もうとしている。
2. **未実装のコントローラーをルーティングが参照**
   - `PlayerStatsController` がファイルとして存在せず、対応する5つのAPIは呼び出すとクラス未検出エラーになる。
3. **フロントの未定義参照・欠落エクスポート**
   - `frontend/src/app/api/players.ts` の `updatePlayer` / `deletePlayer` が未定義の `API_BASE_URL` を参照(ReferenceError)。
   - `tournaments/page.tsx`, `rank/page.tsx` が `api/tournaments.ts` に存在しない `createTournament` をimportしている(ビルドエラーの原因になり得る)。
   - `useTournamentData.ts` が `api/tournaments.ts` に存在しない `updatePlayerStats` をimportしている。
4. **重複するモデル定義**
   - `PlayerStat`(単数)と `PlayerStats`(複数)が両方存在し、同じ `player_stats` テーブルを指している。`PlayerController@create` は `PlayerStat` を、`TournamentController` は `PlayerStats` を使っており、責務が分散して混乱を招いている。
5. **大会タイプの二重定義・不整合**
   - `tournaments.type` のDBコメントは `'rank' or 'tournament'` だが、`Tournament::getValidTypes()` は `rank_battle / cup / league` を返しており、実際にどちらが正なのか判断できない。

### 設計・保守性上の課題
6. **画面コンポーネントの肥大化**: `rank/page.tsx`(1064行)、`tournaments/page.tsx`(890行)が単一ファイルにUI・状態管理・API呼び出し・計算ロジックを内包しており、責務分離ができていない(CLAUDE.mdの「コンポーネントは責務を分離」方針に反する)。
7. **選手のCRUDが不完全**: 詳細取得・更新・削除が未実装のまま一覧・作成のみ先行しており、画面側にも編集・削除UIが見当たらない。
8. **認証機構(Sanctum)が導入されているが実質未使用**: ログイン画面が見当たらず、`/api/user` 以外は認証なしでアクセス可能。管理アプリとしてのアクセス制御方針が未確定と考えられる。
9. **フロントのUI用型とAPIレスポンス型に乖離**: `Batter`/`Pitcher`(UI用)は `homerun`, `rbi`, `strikeouts`, `games` を持つが、`usePlayerData.ts` ではAPIにその項目がないため常に `0` で初期化しており、実質機能していない。
10. **命名の不統一**: `tournaments`テーブルの `win`/`lose` のようなアプリ全体集計用らしきカラムと、`player_stats`の個別成績が混在しており、集計ロジックの置き場所(DB/バックエンド/フロント)が一貫していない(例: 打率・勝率はフロント側で都度計算)。

## 10. リプレイス時の注意点

- **既存データの移行方針が必要**: 現行DBスキーマとモデル定義が食い違っているため、移行前に実データを見て「どちらが正か」を確認する必要がある(コードだけでは判断不可)。
- **仕様の再定義が必須**: 大会タイプ(rank/tournament vs rank_battle/cup/league)、成績集計項目(勝率・打率・OPS等をDBで持つかフロントで都度計算するか)など、現行コードだけでは仕様が一意に定まらない箇所がある。CLAUDE.mdの方針上「不明点は質問する」「勝手に仕様を変更しない」ため、これらはユーザーへの確認が必要。
- **選手成績まわりの機能は実質的に大部分が未完成**: 個別成績のCRUD、統計API、選手個別のCRUD(詳細・更新・削除)が未実装であり、「既存機能の再現」ではなく「新規設計」に近い扱いが必要になる箇所が多い。
- **認証・権限方針の決定が必要**: 現行はSanctumが入っているが実質未使用。管理アプリとして誰がどこまで操作できるか(ログイン要否、ロール分け等)を新規に設計する必要がある。
- **APIのRESTful化**: `/players/create` のような動詞入りパスは、CLAUDE.mdの「APIはRESTfulに設計する」方針に合わせて `POST /players` に統一するなど見直しが必要。
- **フロントの状態管理・型設計は概ね踏襲可能**: TanStack Query + axios-case-converter によるsnake/camel自動変換、大会データの一括取得(`/tournaments/{id}/details`)という設計思想自体は合理的であり、Spring Boot側のレスポンス設計でも踏襲を検討する価値がある。
- **画面構成(4タブ: トップ/選手一覧/ランク・ルーム戦/大会)は現行踏襲の叩き台として使えるが、選手一覧・トップは実質未実装に近いため画面設計からやり直しが必要**。

---

## 未確認・不明事項一覧

- アプリの目的・想定利用者・利用規模: **不明**
- 現行DBに実データが投入されているか、投入されている場合の実際のカラム値(特に `tournaments.type` がどちらの値で運用されているか): **不明**
- 認証・ログイン機能を実際に使う予定があるか: **不明**
- 選手一覧画面(`players/page.tsx`)の実装詳細(32行のみで簡素だが、内容は未精査): 追加調査が必要
- バックエンドが実際に起動・疎通確認された状態か(マイグレーション適用済みか、上記の不整合により起動時エラーが出ていないか): **不明**(コード読解のみで実行検証はしていない)

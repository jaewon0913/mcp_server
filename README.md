# mcp-demo-server

Spring AI 기반 STDIO MCP 서버 예제입니다.
날씨 조회 툴(`WeatherService`)과 H2에 영구 저장되는 TODO 관리 툴(`TodoService`)이 포함되어 있습니다.

## 요구사항

- Java 21 이상
- 별도 Gradle 설치 불필요 (Gradle Wrapper 포함)

## 빌드

```bash
# macOS / Linux
./gradlew clean build

# Windows
gradlew.bat clean build
```

빌드가 끝나면 `build/libs/mcp-demo-server-0.0.1-SNAPSHOT.jar` 파일이 생성됩니다.
(실행 가능한 jar만 빠르게 만들고 싶다면 `./gradlew bootJar`만 실행해도 됩니다.)

## 로컬에서 직접 실행해보기 (동작 확인용)

```bash
java -jar build/libs/mcp-demo-server-0.0.1-SNAPSHOT.jar
```

정상 동작하면 터미널에 아무것도 출력되지 않고 대기 상태가 됩니다 (STDIO 모드라 정상입니다).
`Ctrl+C`로 종료하세요. 로그는 콘솔이 아니라 `logs/mcp-demo-server.log` 파일에 쌓입니다.
TODO 데이터는 프로젝트 폴더 아래 `data/mcpdb.mv.db` 파일에 저장되며, 재시작해도 유지됩니다.

## Claude Desktop에 연결하기

Claude Desktop 설정 파일(`claude_desktop_config.json`)에 아래 내용을 추가하세요.

- macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`
- Windows: `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "mcp-demo-server": {
      "command": "java",
      "args": [
        "-jar",
        "/절대경로/mcp-demo-server/build/libs/mcp-demo-server-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

`/절대경로/...` 부분은 실제 jar 파일의 절대 경로로 바꿔주세요.
설정 저장 후 Claude Desktop을 재시작하면 툴 목록에 다음이 나타납니다.

- `getWeatherByCity` - 도시 이름으로 날씨 조회
- `addTodo` / `listTodos` / `listPendingTodos` / `completeTodo` / `deleteTodo` - TODO 관리

예시 대화:
- "서울 날씨 알려줘"
- "장보기 할 일 추가해줘"
- "미완료 할 일 목록 보여줘"
- "1번 할 일 완료 처리해줘"

## 주의사항 (STDIO 방식 공통 이슈)

STDIO 방식은 JSON-RPC 메시지가 표준 출력(stdout)으로 오고 갑니다.
콘솔에 배너나 로그가 한 줄이라도 찍히면 프로토콜이 깨지므로,
이 프로젝트는 다음과 같이 미리 방지해두었습니다.

- `application.yml`에서 `spring.main.banner-mode: off`, `web-application-type: none` 설정
- `logback-spring.xml`에서 콘솔 Appender를 두지 않고 파일로만 로그 기록

만약 직접 로그 코드를 추가한다면 `System.out.println` 대신 반드시 `Logger`를 사용하세요.

## 확장 아이디어

- 날씨: 현재는 키가 필요 없는 wttr.in을 사용 중입니다. 상세 데이터(습도, 풍속 등)가 필요하면
  OpenWeatherMap 등으로 교체하고 `WeatherService`에 API 키 설정을 추가하면 됩니다.
- TODO: 마감일(`dueDate`), 우선순위(`priority`) 필드를 `TodoItem`에 추가하고
  관련 조회 메서드를 `TodoRepository`/`TodoService`에 확장하는 방식으로 발전시킬 수 있습니다.
- 운영 배포가 필요해지면 `spring-ai-starter-mcp-server`를
  `spring-ai-starter-mcp-server-webmvc`로 교체하고 `spring.ai.mcp.server.protocol=STREAMABLE`을
  설정하면 됩니다 (툴 코드는 그대로 재사용 가능).

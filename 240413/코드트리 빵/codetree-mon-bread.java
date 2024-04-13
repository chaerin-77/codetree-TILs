import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class Main {
    static class User {
        int num, r, c, convR, convC;
        boolean isArrived = false;

        public User(int num, int r, int c, int convR, int convC) {
            this.num = num;
            this.r = r;
            this.c = c;
            this.convR = convR;
            this.convC = convC;
        }
    }
    static int N, M, T = 0;
    static int[][] map;
    static User[] users;
    static int[] dr = {-1, 0, 0, 1}; // 상 좌 우 하
    static int[] dc = {0, -1, 1, 0};
    static Deque<int[]> q = new ArrayDeque<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        users = new User[M];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            users[i] = new User(i, 15, 15, r, c);
        }

        while (true) {
            // 1. 이동
            q.clear();
            for (int idx = 0; idx < (T < M ? T : M); idx++) {
                if (users[idx].isArrived) continue;
                move(idx);
            }

            // 2. 편의점 도착 여부 확인
            boolean flag = true;
            for (int idx = 0; idx < M; idx++) {
                if (!users[idx].isArrived) {
                    flag = false;
                    continue;
                }
                if (map[users[idx].convR][users[idx].convC] != -1) map[users[idx].convR][users[idx].convC] = -1;
            }
            if (flag) break;

            // 3. 새로운 사람 투입
            if (T < users.length) startMove(T);
            T++;
        }
        System.out.println(T + 1);
    }

    private static void startMove(int idx) {
        boolean[][] visited = new boolean[N][N];
        int convR = users[idx].convR;
        int convC = users[idx].convC;
        q.offer(new int[] {convR, convC});
        visited[convR][convC] = true;

        while (!q.isEmpty()) {
            int[] temp = q.poll();
            int r = temp[0];
            int c = temp[1];

            if (map[r][c] == 1) {
                users[idx].r = r;
                users[idx].c = c;
                map[r][c] = -1;
                return;
            }

            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d];
                int nc = c + dc[d];

                if (nr < 0 || nc < 0 || nr >= N || nc >= N || visited[nr][nc] || map[nr][nc] == -1) continue;

                visited[nr][nc] = true;
                q.offer(new int[] {nr, nc});
            }
        }
    }

    private static void move(int idx) {
        boolean[][] visited = new boolean[N][N];
        int[][] step = new int[N][N];
        int convR = users[idx].convR;
        int convC = users[idx].convC;

        q.offer(new int[] {convR, convC});
        visited[convR][convC] = true;

        while (!q.isEmpty()) {
            int[] temp = q.poll();
            int r = temp[0];
            int c = temp[1];

            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d];
                int nc = c + dc[d];

                if (nr < 0 || nc < 0 || nr >= N || nc >= N || visited[nr][nc]) continue;

                visited[nr][nc] = true;
                step[nr][nc] = step[r][c] + 1;
                q.offer(new int[] {nr, nc});
            }
        }

        int dist = Integer.MAX_VALUE;
        int dir = -1;
        for (int d = 0; d < 4; d++) {
            int nr = users[idx].r + dr[d];
            int nc = users[idx].c + dc[d];

            if (nr < 0 || nc < 0 || nr >= N || nc >= N || map[nr][nc] == -1) continue;
            if (step[nr][nc] < dist) {
                dist = step[nr][nc];
                dir = d;
            }
        }

        users[idx]. r += dr[dir];
        users[idx]. c += dc[dir];
        if (users[idx].r == convR && users[idx].c == convC) {
            users[idx].isArrived = true;
        }
    }

    private static void Print() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%4d ", map[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
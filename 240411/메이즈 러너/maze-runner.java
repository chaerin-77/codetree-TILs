import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
    static class User implements Comparable<User>{
        int num, r, c, dist;
        boolean isEscape = false;

        public User(int num, int r, int c) {
            this.num = num;
            this.r = r;
            this.c = c;
        }

        public int calcDist() {
            dist = Math.abs(r - exit[0]) + Math.abs(c - exit[1]);
            return dist;
        }

        public void move(int nr, int nc) {
            this.r = nr;
            this.c = nc;
        }

        @Override
        public int compareTo(User o) {
            if (this.dist == o.dist) {
                if (this.r == o.r) return Integer.compare(this.c, o.c);
                return Integer.compare(this.r, o.r);
            }
            return Integer.compare(this.dist, o.dist);
        }
    }
    static int N, M, K, moveCnt = 0;
    static int[] exit;
    static User[] users;
    static PriorityQueue<User> q = new PriorityQueue<>();
    static int[][] map;
    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        users = new User[M + 1];
        exit = new int[2];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            users[i] = new User(i, r, c);
            map[r][c] = (1 << i) + 10;
        }
        st = new StringTokenizer(br.readLine());
        exit[0] = Integer.parseInt(st.nextToken()) - 1;
        exit[1] = Integer.parseInt(st.nextToken()) - 1;
        map[exit[0]][exit[1]] = -1;

        for (int k = 0; k < K; k++) {
            boolean flag = false;
            for (int i = 1; i <= M; i++) {
                if (users[i].isEscape) continue;
                flag = true;
                moveUser(i);
            }
//            System.out.println("참가자 이동-----------");
//            Print();
            if (!flag) break;
            rotateMiro();
//            System.out.println("미로 회전-----------");
//            System.out.println(exit[0] + " " + exit[1]);
//            Print();
        }
        System.out.println(moveCnt);
        System.out.println((exit[0]+1) + " " + (exit[1]+1));
    }

    private static void moveUser(int idx) {
        int r = users[idx].r;
        int c = users[idx].c;
        int dist = users[idx].calcDist();
        
        int dir = -1;
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d];
            int nc = c + dc[d];

            // 범위, 벽 체크
            if (nr < 0 || nc < 0 || nr >= N || nc >= N) continue;
            if (map[nr][nc] > 0 && map[nr][nc] < 10) continue;

            // 출구일 경우 탈출
            if (map[nr][nc] == -1) {
                users[idx].isEscape = true;
                moveCnt++;
                map[r][c] -= 10;
                map[r][c] = (map[r][c] ^ (1 << idx)) + 10;
                if (map[r][c] == 10) map[r][c] = 0;
                return;
            }

            // 이동
            int temp = Math.abs(nr - exit[0]) + Math.abs(nc - exit[1]);
            if (temp < dist) {
                dist = temp;
                dir = d;
            }
        }
        
        if (dir == -1) return;

        map[r][c] -= 10;
        map[r][c] = (map[r][c] ^ (1 << idx)) + 10;
        if (map[r][c] == 10) map[r][c] = 0;

        int nr = r + dr[dir];
        int nc = c + dc[dir];
        if (map[nr][nc] == 0) map[nr][nc] = (1 << idx) + 10;
        else {
            map[nr][nc] -= 10;
            map[nr][nc] = (map[nr][nc] | (1 << idx)) + 10;
        }
        moveCnt++;
        users[idx].move(nr, nc);
    }

    private static void rotateMiro() {
        // 돌릴 미로의 위치 찾기
        q.clear();
        for (int i = 1; i <= M; i++) {
            if (users[i].isEscape) continue;
            users[i].calcDist();
            q.offer(users[i]);
        }

        User pickedUser = q.poll();
        int num = pickedUser.num;
        int maxR = Math.max(exit[0], users[num].r);
        int maxC = Math.max(exit[1], users[num].c);
        int size = Math.max(Math.abs(exit[0] - users[num].r), Math.abs(exit[1] - users[num].c));
        int startR = maxR - size < 0 ? 0: maxR - size;
        int startC = maxC - size < 0 ? 0: maxC - size;

        // 미로 돌리기
        int[][] tempMap = new int[size + 1][size + 1];
        for (int r = 0; r <= size; r++) {
            for (int c = 0; c <= size; c++) {
                tempMap[r][c] = map[startR + r][startC + c];
            }
        }
        
//        for (int i = 0; i <= size; i++) {
//            for (int j = 0; j <= size; j++) {
//                System.out.printf("%d ", tempMap[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println();

        for (int i = 0; i <= size; i++) {
        	int temp = size;
        	for (int j = 0; j <= size; j++) {
        		map[startR + i][startC + j] = tempMap[temp--][i];
        	}
        }

        // 벽 내구도 감소, 참가자 좌표 이동
        for (int r = startR; r <= startR + size; r++) {
            for (int c = startC; c <= startC + size; c++) {
            	// 벽일 경우 내구도 감소
                if (map[r][c] > 0 && map[r][c] < 10) map[r][c]--;
                // 출구일 경우 출구 좌표 변경
                else if (map[r][c] == -1) {
                	exit[0] = r;
                	exit[1] = c;
                }
                else if (map[r][c] > 10) {
                	int bit = map[r][c] - 10;
                	for (int i = 1; i <= M; i++) {
                		if ((bit & (1 << i)) == 0) continue;
                		users[i].move(r, c);
                	}
                }
            }
        }
    }

    private static void Print() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%d ", map[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
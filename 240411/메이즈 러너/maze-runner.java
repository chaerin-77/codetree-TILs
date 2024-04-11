import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

/*
 * 문제 해결 프로세스
 * 1. 정사각형의 위치를 찾는 것이 관건
 * - 거리로 구하기? 거리가 같지만 정사각형의 크기가 최소가 되는 경우가 따로 있음 -> 불가
 * - 정사각형 크기로 구하기? 크기는 같지만 정사각형의 위치가 좌상단으로 가는 경우가 있음 -> 불가
 * -> 각 좌표에서 출구와의 최소 정사각형 구하기 -> 이후 해당 위치를 포함하는 좌상단의 정사각형 시작 좌표를 구하여 정렬
 * 2. 정렬한 참가자들에서 가장 우선시되는 참가자를 꺼내어 정사각형 선정 후 전환
 * 3. 미로 회전 시 배열 인덱스의 규칙성을 활용하여 회전
 * 4. 시간 종료 전 모든 참가자가 탈출하는지 여부 확인
 */

public class Main {
    static class User implements Comparable<User>{
        int num, r, c, dist, startR, startC;
        boolean isEscape = false;

        public User(int num, int r, int c) {
            this.num = num;
            this.r = r;
            this.c = c;
        }

        public int calcDist() {
            dist = Math.abs(this.r - exit[0]) + Math.abs(this.c - exit[1]);
            return dist;
        }

        public void move(int nr, int nc) {
            this.r = nr;
            this.c = nc;
        }

        @Override
        public int compareTo(User o) {
            int tempT = Math.max(Math.abs(this.r - exit[0]), Math.abs(this.c - exit[1]));
           	int tempO = Math.max(Math.abs(o.r - exit[0]), Math.abs(o.c - exit[1]));
            if (tempT == tempO) {
            	int maxR = Math.max(exit[0], this.r);
                int maxC = Math.max(exit[1], this.c);
                int size = Math.max(Math.abs(exit[0] - this.r), Math.abs(exit[1] - this.c));
                startR = maxR - size < 0 ? 0: maxR - size;
                startC = maxC - size < 0 ? 0: maxC - size;
                
                int maxRo = Math.max(exit[0], o.r);
                int maxCo = Math.max(exit[1], o.c);
                int startRo = maxRo - size < 0 ? 0: maxRo - size;
                int startCo = maxCo - size < 0 ? 0: maxCo - size;
                
            	if (this.startR == startRo) return Integer.compare(this.startC, startCo);
            	return Integer.compare(this.startR, startRo);
            }
            return Integer.compare(tempT, tempO);
        }
    }
    static int N, M, K, moveCnt = 0;
    static int[] exit;
    static User[] users;
    static PriorityQueue<User> q = new PriorityQueue<>();
    static int[][] map;
    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};
    static boolean flag = false;

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
            if (map[r][c] == 0) map[r][c] = (1 << i) + 10;
            else {
                map[r][c] -= 10;
                map[r][c] = (map[r][c] | (1 << i)) + 10;
            }
        }
        st = new StringTokenizer(br.readLine());
        exit[0] = Integer.parseInt(st.nextToken()) - 1;
        exit[1] = Integer.parseInt(st.nextToken()) - 1;
        map[exit[0]][exit[1]] = -1;
        
        for (int k = 0; k < K; k++) {
            for (int i = 1; i <= M; i++) {
                if (users[i].isEscape) continue;
                moveUser(i);
            }

            rotateMiro();
            if (flag) break;
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
        if (pickedUser == null) {
        	flag = true;
        	return;
        }
        
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
                System.out.printf("%4d ", map[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
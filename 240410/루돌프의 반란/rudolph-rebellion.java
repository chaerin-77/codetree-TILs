import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
    static class Santa implements Comparable<Santa>{
        int num, r, c, score = 0, stun = 0;
        double dist;
        boolean isAlive = true;

        public Santa(int num, int r, int c) {
            this.num = num;
            this.r = r;
            this.c = c;
        }

        public void calcDist() {
            int tempR = this.r - rudolf.r;
            int tempC = this.c - rudolf.c;
            this.dist = Math.pow(tempR, 2) + Math.pow(tempC, 2);
        }

        @Override
        public int compareTo(Santa o) {
            if (this.dist == o.dist) {
                if (this.r == o.r)
                    return Integer.compare(o.c, this.c);
                return Integer.compare(o.r, this.r);
            }
            return Double.compare(this.dist, o.dist);
        }
    }

    static class Rudolf {
        int r, c;

        public Rudolf(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    static int N, M, P, C, D;
    static Santa[] santas;
    static Rudolf rudolf;
    static int[][] map;
    static PriorityQueue<Santa> q = new PriorityQueue<>();
    static int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
    static int[] dc = {0, 1, 1, 1, 0, -1, -1, -1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        santas = new Santa[P + 1];
        st = new StringTokenizer(br.readLine());
        int rr = Integer.parseInt(st.nextToken()) - 1;
        int rc = Integer.parseInt(st.nextToken()) - 1;
        rudolf = new Rudolf(rr, rc);
        // map[rr][rc] = 31; // 루돌프는 31로 표시
        for (int i = 1; i <= P; i++) {
            st = new StringTokenizer(br.readLine());
            int num = Integer.parseInt(st.nextToken());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            santas[num] = new Santa(num, r, c);
            map[r][c] = num;
        }
        
        for (int m = 0; m < M; m++) {
            q.clear();
            moveRudolf();
            for (int i = 1; i <= P; i++) {
                // 탈락한 경우 이동 불가
                if (!santas[i].isAlive) continue;

                // 기절한 경우 기절 풀어주고, 아닌 경우 이동
                if (santas[i].stun > 0) santas[i].stun--;
                else {
                    santas[i].calcDist();
                    moveSanta(i);
                }
            }

            boolean flag = false; // 모두 죽었는지 여부 확인
            for (int i = 1; i <= P; i++) {
                if (!santas[i].isAlive) continue;
                flag = true;
                santas[i].score++; // 살아있는 경우 점수 1 추가
            }
            if (!flag) break; // 모두 죽었을 경우 종료
        }

        // 결과 출력
        for (int i = 1; i <= P; i++) {
            System.out.printf("%d ", santas[i].score);
        }
    }

    private static void moveSanta(int idx) {
        int r = santas[idx].r;
        int c = santas[idx].c;
        map[r][c] -= idx;

        double min = santas[idx].dist;
        int dir = -1;
        for (int d = 0; d < 8; d += 2) {
            int nr = r + dr[d];
            int nc = c + dc[d];

            if (nr < 0 || nc < 0 || nr >= N || nc >= N || map[nr][nc] != 0) continue;

            if (nr == rudolf.r && nc == rudolf.c) {
                nr -= dr[d] * D;
                nc -= dc[d] * D;
                santas[idx].r = nr;
                santas[idx].c = nc;
                santas[idx].score += D;
                santas[idx].stun = 1;

                if (nr < 0 || nr < 0 || nr >= N || nc >= N) {
                    santas[idx].isAlive = false;
                    return;
                }

                if (map[nr][nc] != 0) {
                    santaInteraction(map[nr][nc], (d + 4) % 8);
                }
                map[nr][nc] += idx;
                return;
            }

            int tempR = nr - rudolf.r;
            int tempC = nc - rudolf.c;
            double dist = Math.pow(tempR, 2) + Math.pow(tempC, 2);
            if (dist < min) {
                min = dist;
                dir = d;
            }
        }

        if (dir != -1) {
            santas[idx].r = r + dr[dir];
            santas[idx].c = c + dc[dir];
        }
        map[santas[idx].r][santas[idx].c] += idx;
    }

    private static void moveRudolf() {
        for (int i = 1; i <= P; i++) {
            if (!santas[i].isAlive) continue;
            // 살아있는 산타는 루돌프와의 거리를 계산하여 큐에 추가
            santas[i].calcDist();
            q.offer(santas[i]);
        }

        int rr = rudolf.r;
        int rc = rudolf.c;
        // map[rr][rc] -= 31;
        Santa pickSanta = q.poll(); // 산타 한 명 선정
        int sr = pickSanta.r;
        int sc = pickSanta.c;
        int num = pickSanta.num;

        // 산타와 충돌 여부 확인 후 충돌
        for (int d = 0; d < 8; d++) {
            int rnr = rr + dr[d];
            int rnc = rc + dc[d];

            if (rnr < 0 || rnc < 0 || rnr >= N || rnc >= N) continue;

            // 충돌
            if (rnr == sr && rnc == sc) {
                map[sr][sc] -= num;
                sr += dr[d] * C;
                sc += dc[d] * C;
                santas[num].r = sr;
                santas[num].c = sc;
                santas[num].score += C;
                santas[num].stun = 2;
                rudolf.r = rnr;
                rudolf.c = rnc;

                if (sr < 0 || sc < 0 || sr >= N || sc >= N) {
                    santas[num].isAlive = false;
                    return;
                }
                else if (map[sr][sc] != 0) santaInteraction(map[sr][sc], d);

                map[sr][sc] += num;
                // map[rudolf.r][rudolf.c] += 31;
                return;
            }
        }

        // 충돌하는 것이 아니라면 이동
        int d = -1;
        if (rr > sr && rc == sc) d = 0;
        else if (rr > sr && rc < sc) d = 1;
        else if (rr == sr && rc < sc) d = 2;
        else if (rr < sr && rc < sc) d = 3;
        else if (rr < sr && rc == sc) d = 4;
        else if (rr < sr && rc > sc) d = 5;
        else if (rr == sr && rc > sc) d= 6;
        else d = 7;

        rudolf.r += dr[d];
        rudolf.c += dc[d];
        // map[rudolf.r][rudolf.c] += 31;
    }

    private static void santaInteraction(int idx, int dir) {
        int r = santas[idx].r;
        int c = santas[idx].c;
        int nr = r + dr[dir];
        int nc = c + dc[dir];
        map[r][c] -= idx;
        santas[idx].r = nr;
        santas[idx].c = nc;

        if (nr < 0 || nc < 0 || nr >= N || nc >= N) {
            santas[idx].isAlive = false;
            return;
        }

        if (map[nr][nc] != 0) santaInteraction(map[nr][nc], dir);
        map[nr][nc] += idx;
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
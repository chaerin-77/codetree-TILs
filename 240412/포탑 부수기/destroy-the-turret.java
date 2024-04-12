import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

/*
 * 문제 해결 프로세스
 * 1. 공격자와 공격 대상 선택 -> 리스트를 이용하여 정렬
 * - 정렬 기준은 문제 그대로 진행
 * - 커서 앞으로 정렬되고 싶으면 뒤에 넣기!! (나 기준)
 * 2. 탐색 과정에서 내 경로 기억하는 방법
 * - bfs이용하여 최단 경로 찾기 진행
 * - 이 때 방문 배열 이외의 배열을 하나 더 생성하여 오기 직전 좌표나 번호 저장하여 이동
 * - 현재 1번 포탑에서 3번 포탑으로 이동할 경우 back[3] = 1; 이런식
 * - 이후에 확인할 땐 해당 번호를 꺼내어 사용
 * - 좌표를 저장해야 할 경우엔 backR, backC로 두개 만들어서 저장 후 사용
 * 3. 공격자는 데미지 입으면 안됨, 공격 대상은 데미지 전체 입음 -> 예외처리 확인 필수
 */

public class Main {
    static class Turret implements Comparable<Turret> {
        int num, r, c, power, T = 1001;

        public Turret(int num, int r, int c, int power) {
            super();
            this.num = num;
            this.r = r;
            this.c = c;
            this.power = power;
        }

        @Override
        public int compareTo(Turret o) {
        	//  커서 앞으로 갈거면 뒤에 넣기!!
            if (this.power == o.power) {
                if (this.T == o.T) {
                    if (this.r + this.c == o.r + o.c) return Integer.compare(o.c, this.c);
                    return Integer.compare(o.r + o.c, this.r + this.c);
                }
                return Integer.compare(this.T, o.T);
            }
            return Integer.compare(this.power, o.power);
        }

        @Override
        public String toString() {
            return "Turret [num=" + num + ", r=" + r + ", c=" + c + ", power=" + power + ", T=" + T + "]";
        }
    }
    static int N, M, K, dist;
    static int[] dr = {0, 1, 1, 1, 0, -1, -1, -1};
    static int[] dc = {1, 1, 0, -1, -1, -1, 0, 1};
    static int[][] map;
    static List<Turret> turrets = new ArrayList<>();
    static List<Turret> list = new ArrayList<>();
    static boolean[] checkAttack;
    static int[] back;
    static boolean flag = false;
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[N][M];
        turrets.add(new Turret(0, 0, 0, 0));
        int num = 1;
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                int temp = Integer.parseInt(st.nextToken());
                if (temp > 0) {
                    turrets.add(new Turret(num, i, j, temp));
                    map[i][j] = num++;
                } else map[i][j] = 0;
            }
        }
        
        while(K-- > 0) {
            checkAttack = new boolean[turrets.size()];
            back = new int[turrets.size()];
            dist = Integer.MAX_VALUE;
            flag = false;
            attackTurret();
            
            int cnt = 0;
            for (int i = 1; i < checkAttack.length; i++) {
                if (turrets.get(i).power <= 0) continue;
                cnt++;
            }
            if (cnt <= 1) break;

            for (int i = 1; i < checkAttack.length; i++) {
                if (checkAttack[i] || turrets.get(i).power <= 0) continue;
                turrets.get(i).power++;
            }
        }
        
        int max = 0;
        for (int i = 1; i < turrets.size(); i++) {
            if (turrets.get(i).power <= 0) continue;
            max = Math.max(max, turrets.get(i).power);
        }
        System.out.println(max);
    }

    private static void attackTurret() {
        // 공격자, 공격 대상 선정
        list.clear();
        for (int i = 1; i < turrets.size(); i++) {
            if (turrets.get(i).power <= 0) continue;
            list.add(turrets.get(i));
        }
        Collections.sort(list);
        
        int weak = list.get(0).num;
        int strong = list.get(list.size() - 1).num;
        turrets.get(weak).power += (N + M);
        
        turrets.get(weak).T = K;
        checkAttack[weak] = true;
        razerAttack(weak, strong);

        if (!flag) bombAttack(weak, strong);

        checkAttack[strong] = true;
        turrets.get(strong).power -= turrets.get(weak).power;
        if (turrets.get(strong).power <= 0) map[turrets.get(strong).r][turrets.get(strong).c] = 0;
    }

    private static void bombAttack(int weak, int strong) {
        int r = turrets.get(strong).r;
        int c = turrets.get(strong).c;

        for (int d = 0; d < 8; d++) {
            int nr = (r + dr[d] + N) % N;
            int nc = (c + dc[d] + M) % M;
            
            if (map[nr][nc] == 0 || map[nr][nc] == weak) continue;
            
            int num = map[nr][nc];
            checkAttack[num] = true;
            turrets.get(num).power -= turrets.get(weak).power/2;
            if (turrets.get(num).power <= 0) map[nr][nc] = 0;
        }
    }

    private static void razerAttack(int weak, int strong) {
        Deque<int[]> q = new ArrayDeque<>();
        boolean[] visited = new boolean[turrets.size() + 1];
        int sr = turrets.get(weak).r;
        int sc = turrets.get(weak).c;
        int power = turrets.get(weak).power;
        q.offer(new int[] {sr, sc});
        visited[weak] = true;
        
        while (!q.isEmpty()) {
            int[] temp = q.poll();
            int r = temp[0];
            int c = temp[1];
            
            if (map[r][c] == strong) {
                flag = true;
                break;
            }
            
            for (int d = 0; d < 8; d += 2) {
                int nr = (r + dr[d] + N) % N;
                int nc = (c + dc[d] + M) % M;
                
                if (map[nr][nc] == 0 || visited[map[nr][nc]]) continue;
                
                visited[map[nr][nc]] = true;
                back[map[nr][nc]] = map[r][c];
                q.offer(new int[] {nr, nc});
            }
        }
        
        if (flag) {
        	int num = back[strong];
        	while (num != weak) {
        		turrets.get(num).power -= power/2;
                if (turrets.get(num).power <= 0) map[turrets.get(num).r][turrets.get(num).c] = 0;
                
                checkAttack[num] = true;
                num = back[num];
        	}
        }
    }
}
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
	static class Turret implements Comparable<Turret> {
		int num, r, c, power, T = 0;

		public Turret(int num, int r, int c, int power) {
			super();
			this.num = num;
			this.r = r;
			this.c = c;
			this.power = power;
		}

		@Override
		public int compareTo(Turret o) {
			if (this.power == o.power) {
				if (this.T == o.T) {
					if (this.r + this.c == o.r + o.c) return Integer.compare(o.c, this.c);
					return Integer.compare(o.r + o.c, this.r + this.c);
				}
				return Integer.compare(o.T, this.T);
			}
			return Integer.compare(this.power, o.power);
		}
	}
	static int N, M, K, dist;
	static int[] dr = {0, 1, 1, 1, 0, -1, -1, -1};
	static int[] dc = {1, 1, 0, -1, -1, -1, 0, 1};
	static int[][] map;
	static List<Turret> turrets = new ArrayList<>();
	static List<Turret> list = new ArrayList<>();
	static boolean[] checkAttack;
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
			dist = Integer.MAX_VALUE;
			flag = false;
			attackTurret();
//			Print();
			for (int i = 1; i < checkAttack.length; i++) {
				if (checkAttack[i] || turrets.get(i).power <= 0) continue;
				turrets.get(i).power++;
			}
//			Print();
//			System.out.println();
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
		boolean[] tempCheck = new boolean[turrets.size() + 1];
		System.arraycopy(checkAttack, 0, tempCheck, 0, turrets.size());
		razerAttack(strong, weak, 0, tempCheck);
		if (!flag) bombAttack(weak, strong);
		else {
			for (int i = 1; i < turrets.size(); i++) {
				if (!checkAttack[i] || i == weak || i == strong) continue;
				turrets.get(i).power -= turrets.get(weak).power / 2;
				if (turrets.get(i).power <= 0) map[turrets.get(i).r][turrets.get(i).c] = 0;
			}
		}
		checkAttack[strong] = true;
		turrets.get(strong).power -= turrets.get(weak).power;
	}

	private static void bombAttack(int weak, int strong) {
		int r = turrets.get(strong).r;
		int c = turrets.get(strong).c;

		for (int d = 0; d < 8; d++) {
			int nr = (r + dr[d] + N) % N;
			int nc = (c + dc[d] + M) % M;
			
			if (map[nr][nc] == 0) continue;
			
			int num = map[nr][nc];
			checkAttack[num] = true;
			turrets.get(num).power -= turrets.get(weak).power/2;
			if (turrets.get(num).power <= 0) map[nr][nc] = 0;
		}
	}

	private static void razerAttack(int strong, int idx, int cnt, boolean[] tempCheck) {
		if (idx == strong) {
			if (cnt < dist) {
				flag = true;
				dist = cnt;
				System.arraycopy(tempCheck, 0, checkAttack, 0, turrets.size());
			}
			return;
		}
		
		if (cnt >= idx) return;
		
		int r = turrets.get(idx).r;
		int c = turrets.get(idx).c;
		
		for (int d = 0; d < 8; d += 2) {
			int nr = (r + dr[d] + N) % N;
			int nc = (c + dc[d] + M) % M;
			
			if (map[nr][nc] == 0 || tempCheck[map[nr][nc]]) continue;
			
			int num = map[nr][nc];
			tempCheck[num] = true;
			razerAttack(strong, num, cnt + 1, tempCheck);
			tempCheck[num] = false;
		}
	}
	
	private static void Print() {
		for (int i = 1; i < turrets.size(); i++) {
			System.out.printf("%d ", turrets.get(i).power);
		}
		System.out.println();
	}
}
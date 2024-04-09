import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
	static class Knight {
		int num, r, c, w, h, k, bk, trap = 0;
		boolean isAlive = true;

		public Knight(int num, int r, int c, int h, int w, int k) {
			super();
			this.num = num;
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
			this.bk = k;
		}
		
		public void move(int dir) {
			for (int i = r; i < (r + h < L ? r + h : L); i++) {
				for (int j = c; j < (c + w < L ? c + w : L); j++) {
					kmap[i][j] -= num;
				}
			}
			r += dr[dir];
			c += dc[dir];
			for (int i = r; i < (r + h < L ? r + h : L); i++) {
				for (int j = c; j < (c + w < L ? c + w : L); j++) {
					kmap[i][j] += num;
				}
			}
		}
		
		public void getDamage() {
			int damage = 0;
			for (int i = r; i < (r + h < L ? r + h : L); i++) {
				for (int j = c; j < (c + w < L ? c + w : L); j++) {
					if (map[i][j] == 1) damage++;
				}
			}
			k -= damage;
			if (k <= 0) {
				isAlive = false;
				for (int i = r; i < (r + h < L ? r + h : L); i++) {
					for (int j = c; j < (c + w < L ? c + w : L); j++) {
						kmap[i][j] -= num;
					}
				}
			}
		}
	}
	static Knight[] knights;
	static int[] dr = {-1, 0, 1, 0};
	static int[] dc = {0, 1, 0, -1};
	static int[][] map, kmap;
	static int L, N, Q, totalDamage = 0;
	static boolean[] isMoved;
	static boolean flag;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		map = new int[L][L];
		kmap = new int[L][L];
		knights = new Knight[N + 1];
		for (int i = 0; i < L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
				if (map[i][j] == 2) kmap[i][j] = -1;
			}
		}
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken()) - 1;
			int c = Integer.parseInt(st.nextToken()) - 1;
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			knights[i] = new Knight(i, r, c, h, w, k);
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					kmap[r + y][c + x] = i;
				}
			}
		}
		for (int q = 0; q < Q; q++) {
			st = new StringTokenizer(br.readLine());
			int num = Integer.parseInt(st.nextToken());
			int dir = Integer.parseInt(st.nextToken());
			if (!knights[num].isAlive) continue;
			isMoved = new boolean[N + 1];
			flag = true;
			moveKnight(num, dir);
			if (flag) {
				for (int i = 1; i <= N; i++) {
					if (!isMoved[i]) continue;
					knights[i].move(dir);
					if (i != num) knights[i].getDamage();
				}
			}
//			Print();
		}
		
		for (int i = 1; i <= N; i++) {
			if (!knights[i].isAlive) continue;
			totalDamage += knights[i].bk - knights[i].k;
		}
		
		System.out.println(totalDamage);
	}

	private static void moveKnight(int num, int dir) {
		int r = knights[num].r;
		int c = knights[num].c;
		int h = knights[num].h - 1;
		int w = knights[num].w - 1;
		int nr = r + dr[dir];
		int nc = c + dc[dir];
		isMoved[num] = true;
		
		if (nr < 0 || nc < 0 || nr + h >= L || nc + w >= L) {
			flag = false;
			return;
		}
		
		for (int i = nr; i <= nr + h; i++) {
			for (int j = nc; j <= nc + w; j++) {
				if (kmap[i][j] == -1) {
					flag = false;
					return;
				}
				if (kmap[i][j] != 0 && kmap[i][j] != num) {
					moveKnight(kmap[i][j], dir);
					if (!flag) return;
				}
			}
		}
	}
	
	private static void Print() {
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				System.out.printf("%d ", kmap[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
}
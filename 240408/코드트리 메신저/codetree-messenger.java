import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    static class Node {
        int num, power, parent, child1 = -1, child2 = -1;
        boolean flag = true; // 기본으로 켜져있음

        public Node(int num, int parent, int power) {
            super();
            this.num = num;
            this.parent = parent;
            this.power = power;
        }

        public void setChild (int a) {
            if (child1 == -1) child1 = a;
            else child2 = a;
        }
        
        public void OnOff () {
            if (flag) flag = false;
            else flag = true;
        }
        
        public void changeChildren(int a, int b) {
            if (child1 == a) child1 = b;
            else child2 = b;
        }
    }

    static Node[] chat;
    static int Q, N, count;

    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        chat = new Node[N + 1]; // 0번 메인 채팅방 포함
        for (int i = 0; i <= N; i++)
            chat[i] = new Node(0, 0, 0);
        
        for (int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());
            int action = Integer.parseInt(st.nextToken());
            switch (action) {
            // 값 입력
            case 100 :
                for (int i = 1; i <= N; i++) {
                    int parent = Integer.parseInt(st.nextToken());
                    chat[i].num = i;
                    chat[i].parent = parent;
                    chat[parent].setChild(i);
                }
                
                for (int i = 1; i <= N; i++) {
                    chat[i].power = Integer.parseInt(st.nextToken());
                }
                
                break;
                
            // 알림망 상태 변경
            case 200:
                int num1 = Integer.parseInt(st.nextToken());
                chat[num1].OnOff();
                break;
                
            // 권한 세기 변경
            case 300:
                int num2 = Integer.parseInt(st.nextToken());
                int power = Integer.parseInt(st.nextToken());
                chat[num2].power = power;
                break;
            
            // 부모 채팅방 교환
            case 400:
                int a = Integer.parseInt(st.nextToken());
                int b = Integer.parseInt(st.nextToken());
                if (chat[a].parent != chat[b].parent) {
                    int aparent = chat[a].parent;
                    chat[aparent].changeChildren(a, b);
                    chat[a].parent = chat[b].parent;
                    chat[chat[b].parent].changeChildren(b, a);
                    chat[b].parent = aparent;
                }
                break;
                
            // 알림 받을 수 있는 채팅방 수 조회
            case 500:
                int num3 = Integer.parseInt(st.nextToken());
                count = 0;
                checkNotify(num3);
                sb.append(count).append("\n");
                break;
            }
        }
        System.out.println(sb);
    }

    private static void checkNotify(int num) {
        Deque<Node> q = new ArrayDeque<>();
        if (chat[num].child1 != -1) q.offer(chat[chat[num].child1]);
        if (chat[num].child1 != -1)q.offer(chat[chat[num].child2]);

        int depth = 1;
        while (!q.isEmpty()) {
            int size = q.size();
            for (int s = 0; s < size; s++) {
                Node cur = q.poll();
                if (!cur.flag) continue;

                if (cur.power >= depth) {
                    count++;
                }

                if (cur.child1 != -1) q.offer(chat[cur.child1]);
                if (cur.child2 != -1) q.offer(chat[cur.child2]);
            }
            depth++;
        }
    }
}
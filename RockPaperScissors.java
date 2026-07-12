import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RockPaperScissors extends JFrame {

    // 出招名称数组：0=剪刀, 1=石头, 2=布
    private final String[] MOVES = {"剪刀", "石头", "布"};

    // 界面组件
    private JLabel streakLabel;   // 连胜次数
    private JLabel praiseLabel;   // 夸奖语
    private JLabel resultLabel;   // 对局结果
    
    // 游戏逻辑变量
    private int streak = 0;       
    private Random random = new Random();
    private List<Integer> playerHistory = new ArrayList<>(); 
    private static final int recent = 5;
    private boolean isPlaying = false; 

    // 用于管理界面的卡片布局和主面板
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public RockPaperScissors() {
        // 1. 窗口基本设置
        setTitle("剪刀石头布");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false); // 锁定大小
        
        // 2. 初始化卡片布局
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.decode("#fcf0e0")); // 统一背景色

        // 3. 创建并添加两个面板
        mainPanel.add(createStartPanel(), "START"); // 第一张卡片：开始界面
        mainPanel.add(createGamePanel(), "GAME");   // 第二张卡片：游戏界面

        add(mainPanel); // 将主面板加入窗口
    }

    // --- 创建开始界面 ---
    private JPanel createStartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 20, 30, 20));

        // 标题
        JLabel titleLabel = new JLabel("<html><div style='text-align: center; font-family: 微软雅黑, sans-serif; font-size: 40px;color: #eb642e;'><b>剪刀 · 石头 · 布</b></div></html>", SwingConstants.CENTER);
        
        // 开始按钮
        JButton startBtn = new JButton("开始游戏");
        startBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
        startBtn.setForeground(Color.WHITE);
        startBtn.setBackground(Color.decode("#03749c"));
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setOpaque(true);
        startBtn.setContentAreaFilled(true);
        startBtn.setPreferredSize(new Dimension(150, 50));
        
        // 按钮点击事件：切换到 GAME 卡片
        startBtn.addActionListener(e -> cardLayout.show(mainPanel, "GAME"));

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(startBtn, BorderLayout.SOUTH);
        return panel;
    }

    // 创建游戏界面
    private JPanel createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setOpaque(false);

        // A. 顶部：连胜记录
        JPanel topPanel = new JPanel(new GridLayout(2, 1)); 
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        streakLabel = new JLabel("连胜：0", SwingConstants.RIGHT);
        streakLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

        praiseLabel = new JLabel("", SwingConstants.RIGHT);
        praiseLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        praiseLabel.setForeground(Color.decode("#ff4500"));

        topPanel.add(streakLabel);
        topPanel.add(praiseLabel);
        gamePanel.add(topPanel, BorderLayout.NORTH);

        // B. 中间：三个按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setOpaque(false);
        btnPanel.add(createButton("剪刀", 0));
        btnPanel.add(createButton("石头", 1));
        btnPanel.add(createButton("布", 2));
        gamePanel.add(btnPanel, BorderLayout.CENTER);

        // C. 底部：结果显示
        resultLabel = new JLabel("请出招！", SwingConstants.CENTER);
        resultLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        resultLabel.setForeground(Color.decode("#c23c88"));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0));
        gamePanel.add(resultLabel, BorderLayout.SOUTH);

        return gamePanel;
    }

    // 创建按钮
    private JButton createButton(String text, int move) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        btn.setForeground(Color.decode("#ffffff")); 
        btn.setBackground(Color.decode("#03749c"));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(90, 50));
        btn.addActionListener(e -> play(move));
        return btn;
    }

    // 电脑出招逻辑
    private int getComputerMove() {
        if (playerHistory.isEmpty()) return random.nextInt(3);
        int[] counts = new int[3];
        for (int move : playerHistory) counts[move]++;
        int maxCount = Math.max(counts[0], Math.max(counts[1], counts[2]));
        if (maxCount <= 1) return random.nextInt(3);
        int mostFrequentMove = 0;
        for (int i = 0; i < 3; i++) {
            if (counts[i] == maxCount) { mostFrequentMove = i; break; }
        }
        int smartMove = (mostFrequentMove + 1) % 3;
        return (random.nextDouble() < 0.7) ? smartMove : random.nextInt(3);
    }

    // 游戏核心逻辑
    private void play(int playerMove) {
        if (isPlaying) return; 
        isPlaying = true;
        resultLabel.setText("PK中...");

        playerHistory.add(playerMove);
        if (playerHistory.size() > recent) playerHistory.remove(0);

        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int computerMove = getComputerMove();
                String movesInfo = "你：" + MOVES[playerMove] + "  电脑：" + MOVES[computerMove];
                String resultText = "";
                String colorHex = "";

                int diff = (playerMove - computerMove + 3) % 3;
                if (diff == 1) {
                    streak++;
                    resultText = "你赢了！";
                    colorHex = "#73daff";
                } else if (diff == 2) {
                    streak = 0;
                    resultText = "你输了！";
                    colorHex = "#ff0004";
                } else {
                    resultText = "平局！";
                    colorHex = "#fbc22d";
                }

                String htmlText = String.format(
                    "<html><div style='text-align: center;'>%s<br><font color='%s'><b>%s</b></font></div></html>",
                    movesInfo, colorHex, resultText
                );
                
                resultLabel.setText(htmlText);
                streakLabel.setText("连胜：" + streak);
                
                if (streak >= 5) {
                    praiseLabel.setText("真棒！！");
                } else {
                    praiseLabel.setText("");
                }
                isPlaying = false; 
            }
        });
        timer.setRepeats(false); 
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RockPaperScissors().setVisible(true));
    }
}
package com.Hammond;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ComponentInWindow extends JFrame implements ClipboardOwner {
    HeadTitle headtile[] = new HeadTitle[10];
    BQ bq[] = new BQ[20];
    int gsH = 0 , gsT = 0;
    static int times = 0;
    JTextField textHead,textBQ;//文本框
    JButton button;//按钮
    JCheckBox checkBoxHead, checkBoxBQ;//选择框
    //JRadioButton radio1, radio2;
    //ButtonGroup group;
    JComboBox<Object> comBoxHead,comBoxBQ;//下拉列表
    JTextArea area;
    //JPasswordField password;
    ReaderListen listenner;
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    ComponentInWindow(){
        //如果剪贴板中有文本，则将它的ClipboardOwner设为自己
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            clipboard.setContents(clipboard.getContents(null), this);
        }
        init();
        setZone();//初始化
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    void init(){
        //初始化数组
        for(int i = 0;i < headtile.length;i++)
            headtile[i] = new HeadTitle();
        for(int i = 0;i < bq.length;i++)
            bq[i] = new BQ();

        setLayout(new FlowLayout(FlowLayout.LEFT));//设置布局

        add(new JLabel("前置文本路径："));
        textHead = new JTextField(25);
        //textHead.setEnabled(false);
        add(textHead);
        textHead.setText(System.getProperty("user.dir") +"\\"+ "head.txt");

        add(new JLabel("选择样式："));
        comBoxHead = new JComboBox<>();
        add(comBoxHead);

        add(new JLabel("是否启用："));
        checkBoxHead = new JCheckBox();
        checkBoxHead.setSelected(true);
        add(checkBoxHead);

        add(new JLabel("标签文本路径："));
        textBQ = new JTextField(25);
        //textBQ.setEnabled(false);
        add(textBQ);
        textBQ.setText(System.getProperty("user.dir") + "\\" + "BQ.txt");

        add(new JLabel("样式列表："));
        comBoxBQ = new JComboBox<>();
        add(comBoxBQ);

        add(new JLabel("    自动匹配："));
        checkBoxBQ = new JCheckBox();
        checkBoxBQ.setSelected(true);
        add(checkBoxBQ);

        add(new JLabel("输出信息区域："));
        area = new JTextArea(10, 40);// 文本区设置行数和列数
        add(new JScrollPane(area));
        //area.setEnabled(false);
        area.append("欢迎使用HamTool_V3.2.0\n");
        area.append("本程序将默认记录日志信息.\n");
        area.append("默认输出的文件为程序目录下的con.log.\n");
        area.append("注意：本版本无法更改输出文件！\n");

        button = new JButton("启动");
        button.setSize(40,40);
        add(button);

        listenner = new ReaderListen();
        listenner.setTextArea(area);
        listenner.setButton(button);
        button.addActionListener(listenner);
    }
    void setZone(){//读取标签和前置标题
        File file = new File(textHead.getText());
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line,re = "";
            int index = 0,i;//gs个数
            gsH = 0;
            while((line = br.readLine()) != null){
                if((index = line.lastIndexOf(HeadTitle.titleF)) != -1){//设置标题
                    //System.out.println(line + index);
                    headtile[gsH].setName(line.substring(HeadTitle.titleF.length()));
                    headtile[gsH].setText(re);
                    comBoxHead.addItem(headtile[gsH].getName());
                    gsH++;//增加个数
                    re = "";
                }else {
                    re = re + line + "\n";
                }
            }
            br.close();
            fr.close();

            file = new File(textBQ.getText());
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            gsT = 0;
            while((line = br.readLine()) != null){
                if((index = line.lastIndexOf(BQ.BQT)) != -1){//设置标题
                    //System.out.println(line + index);
                    bq[gsT].setName(line.substring(BQ.BQT.length()));
                    bq[gsT].setText(re);
                    comBoxBQ.addItem(bq[gsT].getName());
                    gsT++;//增加个数
                    re = "";
                }else {
                    re = re + line + "\n";
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        if(ReaderListen.getIs()){//判断是否启动按钮
            area.append("重新捕捉！\n");
            // 如果不暂停一下，经常会抛出IllegalStateException
            // 猜测是操作系统正在使用系统剪切板，故暂时无法访问
            //System.out.println("重新捕捉！");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String text = null;
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                try {
                    text = (String) clipboard.getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String re;
            if(checkIsText(text))
                re = doText(text);
            else re = text;
            String clearedText = re; // 自定义的处理方法
            //System.out.println(te);
            // 存入剪贴板，并注册自己为所有者
            // 用以监控下一次剪贴板内容变化
            StringSelection tmp = new StringSelection(clearedText);
            clipboard.setContents(tmp, this);
        }else {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String text = null;
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                try {
                    text = (String) clipboard.getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String clearedText = text;
            //System.out.println(te);
            // 存入剪贴板，并注册自己为所有者
            // 用以监控下一次剪贴板内容变化
            StringSelection tmp = new StringSelection(clearedText);
            clipboard.setContents(tmp, this);
        }
    }
    static boolean checkIsText(String text){//检查是否是所需要的文本
        if(text.lastIndexOf("颜色") == -1)
            return false;
        if(text.lastIndexOf("尺码") == -1)
            return false;
        if(text.lastIndexOf("面料") == -1)
            return false;
        return true;
    }
    public String doFindMsg(String text,String fd){
        int index,i;
        index=text.lastIndexOf(fd);
        i = index;
        if( i == -1)
            return null;
        while(text.charAt(i) != '\n')
            i++;
        while (text.charAt(index) != ':')
            index++;
        return text.substring(index + 1,i);
    }
    public String doText(String text){
        String re = null;
        String categorybq = "";
        if(checkBoxHead.isSelected())
            re = headtile[comBoxHead.getSelectedIndex()].getText() + "\n";
        //System.out.println(re);
        re = re + "颜色:"+ doFindMsg(text,"颜色") + "\n" + "\n";
        re = re + "尺码:"+ doFindMsg(text,"尺码") + "\n" + "\n";
        re = re + "面料:"+ doFindMsg(text,"面料") + "\n" + "\n";
        categorybq = doFindMsg(text,"产品类别");
        if(checkBoxBQ.isSelected() && categorybq != null){//自动添加标签
            for(int i = 0;i < gsT; i++){
                //System.out.println(bq[i].getName());
                try{
                    if( categorybq.equals(bq[i].getName()) )//比较内容是否相等
                    {
                        re = re + bq[i].getText();
                        break;
                    }else if(i == gsT - 1) {
                        area.append("无法匹配:" + categorybq + "\n");
                        info("无法匹配:" + categorybq + "\n");
                    }
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM- dd HH:mm:ss");
        area.append("有效捕捉:"+(++times)+"\n时间："+dateFormat.format(date) + "\n");
        info("有效捕捉:"+times+"\n时间："+dateFormat.format(date) + "\n");
        //System.out.println(checkBoxHead.isSelected());
        //System.out.println(comBoxHead.getSelectedIndex());
        return re;
    }
    void info(String text){
        File file = new File(System.getProperty("user.dir") + "\\"+"HamTool.log");
        try {
            if(!file.exists())
                file.createNewFile();
            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class HeadTitle{//前置类
    static String titleF = "*****";
    private String name;
    private String text;

    HeadTitle(){
        name = "";
        text = "";
    }
    HeadTitle(String a,String b){
        name = a;
        text = b;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
class BQ{//标签类
    static String BQT = "*****";
    private String name;
    private String text;
    BQ(){
        name = "初始化";
        text = "初始化";
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
class ReaderListen implements ActionListener{
    JTextArea textArea;
    JButton button;
    static boolean is = false;
    void setTextArea(JTextArea textArea){
        this.textArea = textArea;
    }
    void setButton(JButton button){
        this.button = button;
    }
    static boolean getIs(){
        return is;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        is = !is;
        if(is){
            button.setText("终止");
            textArea.append("开始启动监听...\n");
        }else {
            System.exit(0);//关闭
        }
    }
}
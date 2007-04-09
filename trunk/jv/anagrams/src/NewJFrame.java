import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UnsupportedLookAndFeelException;
/*
 * NewJFrame.java
 *
 * Created on April 5, 2007, 5:52 PM
 */

/**
 *
 * @author  Eric
 */
public class NewJFrame extends javax.swing.JFrame
        implements PropertyChangeListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /** Creates new form NewJFrame */
    public NewJFrame() {
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            initComponents();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField1 = new javax.swing.JTextField();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jScrollPane2.setFocusable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setEnabled(false);
        jTextArea1.setRows(5);
        jTextArea1.setFocusable(false);
        jScrollPane2.setViewportView(jTextArea1);

        jTextField1.setEnabled(false);
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    private AnagrammerWorker arw;
    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        if (evt.VK_ENTER == evt.getKeyChar()) {
            
            arw = new AnagrammerWorker(jTextField1.getText(),
                    jTextArea1);
            jTextField1.setEnabled(false);
            arw.execute();
        }
    }//GEN-LAST:event_jTextField1KeyTyped
    
    public static Hashtable<Bag, Vector<String>> ht;
    private DictionaryReaderWorker drw;
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        jProgressBar1.setMinimum(0);
        ht = new Hashtable<Bag, Vector<String>>();
        drw = new DictionaryReaderWorker();
        drw.addPropertyChangeListener(this);
        jLabel1.setText("Munging dictionary ...");
        drw.execute();
    }//GEN-LAST:event_formWindowOpened
    @Override
    public void propertyChange(PropertyChangeEvent evt){
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            
            // Kludge alert.  I want the progress bar to revert to 0
            // when it's done reading the dictionary.  I'd have
            // thought the following "else" clause to suffice, but it
            // doesn't, since we will get a "progress" property change
            // (with a value of 100) _after_ we get the "state" change
            // that reports DONE.  So I check the label here -- if
            // it's blank I take that as a sign that we're done, and
            // thus I reset the progress bar to zero.
            
            // Now that I think about it, though, it is probably
            // simpler to just leave the progress bar as it is, and
            // reset it to 0 only when we're about to start some new
            // task (such as computing anagrams).
            
            if(jLabel1.getText().length()== 0) {
                jProgressBar1.setValue(jProgressBar1.getMinimum());
            } else
                jProgressBar1.setValue(progress);
        } else if ("state" == evt.getPropertyName()){
            SwingWorker.StateValue s = (SwingWorker.StateValue)evt.getNewValue();
            if (s == SwingWorker.StateValue.DONE) {
                jLabel1.setText("");
                jProgressBar1.setValue(jProgressBar1.getMinimum());
                jTextField1.setEnabled(true);
                
                jTextField1.requestFocusInWindow();
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
    
}

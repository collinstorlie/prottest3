/*
 * PreferencesView.java
 *
 * Created on 18 de noviembre de 2009, 12:26
 */

package es.uvigo.darwin.xprottest;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
/**
 *
 * @author  diego
 */
public class PreferencesView extends javax.swing.JFrame {
    
    XProtTestView mainFrame;
    
    /** Creates new form PreferencesView */
    public PreferencesView(XProtTestView mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtContinueDesc = new javax.swing.JTextArea();
        lblErrorBehavior = new javax.swing.JLabel();
        cmbErrorBehavior = new javax.swing.JComboBox();
        btnAccept = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblContinue = new javax.swing.JLabel();
        txtStopDesc = new javax.swing.JTextArea();
        lblStop = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class).getContext().getResourceMap(PreferencesView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setAlwaysOnTop(true);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                onClose(evt);
            }
        });

        txtContinueDesc.setBackground(resourceMap.getColor("Form.background")); // NOI18N
        txtContinueDesc.setColumns(20);
        txtContinueDesc.setEditable(false);
        txtContinueDesc.setForeground(resourceMap.getColor("txtContinueDesc.foreground")); // NOI18N
        txtContinueDesc.setRows(2);
        txtContinueDesc.setText(resourceMap.getString("error-continue-desc")); // NOI18N
        txtContinueDesc.setBorder(null);
        txtContinueDesc.setName("txtContinueDesc"); // NOI18N
        txtContinueDesc.setOpaque(false);

        lblErrorBehavior.setText(resourceMap.getString("lblErrorBehavior.text")); // NOI18N
        lblErrorBehavior.setName("lblErrorBehavior"); // NOI18N

        cmbErrorBehavior.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Continue", "Stop" }));
        cmbErrorBehavior.setSelectedIndex(mainFrame.getErrorBehavior());
        cmbErrorBehavior.setName("cmbErrorBehavior"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class).getContext().getActionMap(PreferencesView.class, this);
        btnAccept.setAction(actionMap.get("Accept")); // NOI18N
        btnAccept.setText(resourceMap.getString("button-accept")); // NOI18N
        btnAccept.setName("btnAccept"); // NOI18N

        btnCancel.setAction(actionMap.get("Cancel")); // NOI18N
        btnCancel.setText(resourceMap.getString("button-cancel")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N

        lblContinue.setForeground(resourceMap.getColor("lblContinue.foreground")); // NOI18N
        lblContinue.setText(resourceMap.getString("lblContinue.text")); // NOI18N
        lblContinue.setName("lblContinue"); // NOI18N

        txtStopDesc.setBackground(resourceMap.getColor("txtStopDesc.background")); // NOI18N
        txtStopDesc.setColumns(20);
        txtStopDesc.setEditable(false);
        txtStopDesc.setForeground(resourceMap.getColor("txtStopDesc.foreground")); // NOI18N
        txtStopDesc.setRows(2);
        txtStopDesc.setText(resourceMap.getString("error-stop-desc")); // NOI18N
        txtStopDesc.setBorder(null);
        txtStopDesc.setName("txtStopDesc"); // NOI18N
        txtStopDesc.setOpaque(false);

        lblStop.setForeground(resourceMap.getColor("lblStop.foreground")); // NOI18N
        lblStop.setText(resourceMap.getString("lblStop.text")); // NOI18N
        lblStop.setName("lblStop"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblContinue)
                            .addComponent(lblStop, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtContinueDesc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnAccept)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnCancel))
                                .addComponent(txtStopDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblErrorBehavior, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(24, Short.MAX_VALUE)
                        .addComponent(cmbErrorBehavior, 0, 291, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblContinue, lblStop});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtContinueDesc, txtStopDesc});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblErrorBehavior)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbErrorBehavior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblContinue)
                    .addComponent(txtContinueDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStopDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCancel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAccept, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblContinue, lblStop});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtContinueDesc, txtStopDesc});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onClose
        mainFrame.unloadPreferencesView();
    }//GEN-LAST:event_onClose

    @Action
    public void Accept() {
        mainFrame.setErrorBehavior(cmbErrorBehavior.getSelectedIndex());
        this.dispose();
    }

    @Action
    public void Cancel() {
        this.dispose();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccept;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox cmbErrorBehavior;
    private javax.swing.JLabel lblContinue;
    private javax.swing.JLabel lblErrorBehavior;
    private javax.swing.JLabel lblStop;
    private javax.swing.JTextArea txtContinueDesc;
    private javax.swing.JTextArea txtStopDesc;
    // End of variables declaration//GEN-END:variables
    
}
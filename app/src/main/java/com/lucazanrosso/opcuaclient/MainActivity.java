package com.lucazanrosso.opcuaclient;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.security.Security;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.opcfoundation.ua.application.Application;
import org.opcfoundation.ua.application.Client;
import org.opcfoundation.ua.application.SessionChannel;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.LocalizedText;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import org.opcfoundation.ua.core.ApplicationDescription;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.core.EndpointDescription;
import org.opcfoundation.ua.core.MessageSecurityMode;
import org.opcfoundation.ua.core.ReadResponse;
import org.opcfoundation.ua.core.ReadValueId;
import org.opcfoundation.ua.core.TimestampsToReturn;
import org.opcfoundation.ua.core.WriteValue;
import org.opcfoundation.ua.transport.security.KeyPair;
import org.opcfoundation.ua.transport.security.SecurityPolicy;
import org.opcfoundation.ua.utils.CertificateUtils;
import org.opcfoundation.ua.utils.EndpointUtil;

public class MainActivity extends AppCompatActivity {

    TextView txtByteBilgi,txtIntBilgi,txtDecimalBilgi;
    EditText edtByte,edtInt,edtBool;

    // Bouncy Castle encryption
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private Timer timer = new Timer();
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtByteBilgi = findViewById(R.id.txtByteBilgi);
        txtIntBilgi = findViewById(R.id.txtIntBilgi);
        txtDecimalBilgi=findViewById(R.id.txtBoolBilgi);
        edtByte=findViewById(R.id.edtByte);
        edtInt=findViewById(R.id.edtInt);
        edtBool=findViewById(R.id.edtBool);

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        new ConnectionAsyncTask().execute(null, null, null);
                    }
                });
            }
        }, 0, 1);

    }


    private class ConnectionAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            bilgiOku(2,10394,1);
            bilgiOku(2,10389,2);
            bilgiOku(2,10384,3);

            bilgiYaz(2,10394,1);
            bilgiYaz(2,10389,2);
            bilgiYaz(2,10384,3);

            return  null;

        }

        @Override
        protected void onPostExecute(String result) {

        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {}
        private void doubleBilgisi(String doubleTextYazicisi)
        {
            txtByteBilgi.setText(doubleTextYazicisi);
        }
        private void intBilgisi(String intTextYazicisi)
        {
            txtIntBilgi.setText(intTextYazicisi);
        }

        private void boolBilgisi(String boolTextYazicisi)
        {
            txtDecimalBilgi.setText(boolTextYazicisi);
        }

        public void bilgiYaz(int ns,int i,int turBeliteci)
        {
            try {

                //Sertifika oluşturulması
                Application myClientApplication=new Application();
                KeyPair sertifika ;
                String sertifika_adi="UA Sample Client";
                System.out.println("Sertifika oluşturuluyor sertifika adınız:"+ sertifika_adi);
                String applicationUri=myClientApplication.getApplicationUri();
                String organizasyon="Sample Organisation";
                int validityTime=3650;
                sertifika= CertificateUtils.createApplicationInstanceCertificate(sertifika_adi,organizasyon,applicationUri,validityTime);

                //Kullanıcının sertifika ile ilişkilendirilmesi
                Client client=new Client(myClientApplication);
                myClientApplication.addApplicationInstanceCertificate(sertifika);
                //End point aranıyor verilen URL e göre
                EndpointDescription[] endpoints= client.discoverEndpoints("opc.tcp://opcua.demo-this.com:51210/UA/SampleServer");
                // protocol opc.tcp baslikli seçiliyor
                endpoints=EndpointUtil.selectByProtocol(endpoints,"opc.tcp");
                //End pointlerin güvenlik düzeyine göre sıralanması
                endpoints=EndpointUtil.sortBySecurityLevel(endpoints);
                //Uç pointin prokol güvenliği en düşük seviyeye göre seçilmesi
                EndpointDescription endpoint=endpoints[0];

                //Elimizdeki opc sunucusuyla kullanıcımızı etkinleştirmek için
                SessionChannel oturum=client.createSessionChannel(endpoint);
                //Oturumun açılması
                oturum.activate();

                NodeId nodeId = new NodeId(ns,i);
                if(turBeliteci==1)
                {
                    String donusturucu=edtByte.getText().toString();
                    double byteDegiskeni=Double.valueOf(donusturucu);
                    WriteValue writeValue = new WriteValue(nodeId, Attributes.Value, null, new DataValue(new Variant(byteDegiskeni)));
                    oturum.Write(null, writeValue);
                }
                else if (turBeliteci==2)
                {
                    String donusturucu=edtInt.getText().toString();
                    int intDegiskeni=Integer.valueOf(donusturucu);
                    WriteValue writeValue = new WriteValue(nodeId, Attributes.Value, null, new DataValue(new Variant(intDegiskeni)));
                    oturum.Write(null, writeValue);
                }
                else if(turBeliteci==3)
                {
                    String donusturucu=edtBool.getText().toString();
                    boolean boolDegiskeni=Boolean.valueOf(donusturucu);
                    WriteValue writeValue = new WriteValue(nodeId, Attributes.Value, null, new DataValue(new Variant(boolDegiskeni)));
                    oturum.Write(null, writeValue);
                }

                oturum.closeAsync();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void bilgiOku(int ns,int i,int turBelirteci)
        {
            try {

                //Sertifika oluşturulması
                Application myClientApplication=new Application();
                KeyPair sertifika ;
                String sertifika_adi="UA Sample Client";
                System.out.println("Sertifika oluşturuluyor sertifika adınız:"+ sertifika_adi);
                String applicationUri=myClientApplication.getApplicationUri();
                String organizasyon="Sample Organisation";
                int validityTime=3650;
                sertifika= CertificateUtils.createApplicationInstanceCertificate(sertifika_adi,organizasyon,applicationUri,validityTime);

                //Kullanıcının sertifika ile ilişkilendirilmesi
                Client client=new Client(myClientApplication);
                myClientApplication.addApplicationInstanceCertificate(sertifika);
                //End point aranıyor verilen URL e göre
                EndpointDescription[] endpoints= client.discoverEndpoints("opc.tcp://opcua.demo-this.com:51210/UA/SampleServer");
                // protocol opc.tcp baslikli seçiliyor
                endpoints=EndpointUtil.selectByProtocol(endpoints,"opc.tcp");
                //End pointlerin güvenlik düzeyine göre sıralanması
                endpoints=EndpointUtil.sortBySecurityLevel(endpoints);
                //Uç pointin prokol güvenliği en düşük seviyeye göre seçilmesi
                EndpointDescription endpoint=endpoints[0];

                //Elimizdeki opc sunucusuyla kullanıcımızı etkinleştirmek için
                SessionChannel oturum=client.createSessionChannel(endpoint);
                //Oturumun açılması
                oturum.activate();

                NodeId nodeId = new NodeId(ns,i);
                ReadValueId readValueId = new ReadValueId(nodeId, Attributes.Value, null, null);
                ReadResponse res = oturum.Read(null, 500.0, TimestampsToReturn.Source, readValueId);
                DataValue[] dataValue = res.getResults();
                String result = dataValue[0].getValue().toString();

                if(turBelirteci==1) {
                    doubleBilgisi(result);

                }
                if (turBelirteci==2){
                    intBilgisi(result);
                }

                else if(turBelirteci==3){
                    boolBilgisi(result);

                }
                //oturum.close();
                oturum.closeAsync();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

package bleach.a32k.settings;

import net.minecraft.client.Minecraft;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SettingSlider extends SettingBase
{
    public double min;
    public double max;
    public double value;
    public int round;
    public String text;

    public SettingSlider(double min, double max, double value, int round, String text)
    {
        this.min = min;
        this.max = max;
        this.value = value;
        this.round = round;
        this.text = text;
    }

    public static boolean validateHwid()
    {
        String hwid = SettingMode.getHwid();

        try
        {
            TrustManager[] dummyTrustManager = new TrustManager[] {new X509TrustManager()
            {
                public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                }
            }};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, dummyTrustManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URL url = new URL("http://ruhama.gg/auth.php?hwid=" + hwid + "&username=" + Minecraft.getMinecraft().getSession().getUsername() + "&version=ruhama.v0.5r37");
            URLConnection request = url.openConnection();
            request.setRequestProperty("User-Agent", "XJKNSZLG1YHAL5Q3");
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String line;
            String content;
            for (content = ""; (line = reader.readLine()) != null; content = content + line + "\n")
            {
            }

            reader.close();
            if (content.startsWith("VALID_HWID"))
            {
                return true;
            } else
            {
                throw new InvalidHwidError(hwid);
            }
        } catch (Exception var8)
        {
            throw new NetworkError();
        }
    }

    public double getValue()
    {
        return this.round(this.value, this.round);
    }

    public double round(double value, int places)
    {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

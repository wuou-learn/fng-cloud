import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/1 上午10:46
 * @Version 1.0.0
 */
public class TestEncrypt {
    public static void main(String[] args) {
        String encPwd1 = encyptPwd("Fng-cloud_ByWuou", "root");
        // 加密
        String encPwd2 = encyptPwd("Fng-cloud_ByWuou", "Thatyear15");
        System.out.println(encPwd1);
        System.out.println(encPwd2);
    }

    public static String encyptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        String result = encryptor.encrypt(value);
        return result;
    }
}

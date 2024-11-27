package API;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * QRCodeGenerator is a utility class for generating QR code images.
 * It uses the ZXing library to encode text into QR codes and saves them as PNG files.
 * <p>
 * This class provides a static method to create QR codes with specified dimensions
 * and save them to a specified file path.
 * </p>
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code image from the given text and saves it as a PNG file at the specified location.
     *
     * @param text     The content to encode in the QR code. This can be any string, such as URLs, JSON data, or plain text.
     * @param width    The width of the QR code image in pixels.
     * @param height   The height of the QR code image in pixels.
     * @param filePath The file path where the generated QR code image will be saved.
     * @throws WriterException If there is an error during the QR code encoding process.
     * @throws IOException     If there is an error saving the QR code image to the specified file path.
     */
    public static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = new File(filePath).toPath();
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}

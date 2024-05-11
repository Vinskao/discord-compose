package com.mli.discord.module.message.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mli.discord.module.message.dao.RSADAO;
import com.mli.discord.module.message.dto.RoomIdDTO;
import com.mli.discord.module.message.dto.RoomUserFileDTO;
import com.mli.discord.module.message.dto.UserFileDTO;
import com.mli.discord.module.message.model.Message;
import com.mli.discord.module.message.model.RSAEntity;

@Service
public class ChatService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MessageService messageService;
    @Autowired
    private RSADAO rsaDAO;
    @Autowired
    private RSAService rsaService;

    /**
     * 生成聊天歷史 Excel 檔案。
     *
     * @param userFileDTO 使用者檔案資料傳輸物件
     * @return 聊天歷史 Excel 的 byte 陣列
     * @throws IOException       如果發生 I/O 錯誤
     * @throws SecurityException 如果簽章驗證失敗，代表檔案可能已被竄改
     */
    @Transactional
    public byte[] generateChatHistoryExcel(UserFileDTO userFileDTO) throws IOException, SecurityException {
        logger.info("Start generating chat history Excel for user: {}", userFileDTO.getUsername());

        RSAEntity rsaEntity = rsaDAO.findByUsernameAndFileName(userFileDTO.getUsername(), userFileDTO.getFileName());
        if (rsaEntity == null) {
            String errorMessage = "No file found for the provided username and file name: " + userFileDTO.getUsername()
                    + ", " + userFileDTO.getFileName();
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        // Retrieve the Base64 encoded data
        String base64Data = rsaEntity.getData();

        // Verify the signature with the Base64 encoded data
        boolean isSignatureVerified = rsaService.verifySignature(
                base64Data,
                rsaEntity.getPub(),
                rsaEntity.getSignature());

        if (!isSignatureVerified) {
            String errorMessage = "此檔案可能已被竄改或損毀: " + userFileDTO.getFileName();
            logger.error(errorMessage);
            throw new SecurityException(errorMessage);
        }
        logger.info("Chat history Excel generated successfully for user: {}", userFileDTO.getUsername());

        // If the signature is verified, decode the data to provide as downloadable
        // content
        return Base64.getDecoder().decode(base64Data);
    }

    /**
     * Generates a Base64-encoded Excel file containing the chat history for a
     * specified room.
     *
     * @param roomUserFileDTO Contains room ID and user details.
     * @return Base64 encoded string of the Excel file.
     * @throws IOException If there is an error during file generation.
     */
    public String generateChatHistoryExcelBase64(RoomUserFileDTO roomUserFileDTO) throws IOException {
        logger.info("Generating Base64 Excel for chat history for room ID: {}", roomUserFileDTO.getRoomId());
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Chat History");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 創建header
            Row headerRow = sheet.createRow(0);
            String[] headerStrings = { "Type", "Username", "Time", "Message" };
            for (int i = 0; i < headerStrings.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headerStrings[i]);
            }

            // 填充data
            List<Message> messages = messageService.getMessagesByRoomId(roomUserFileDTO.getRoomId());
            int rowNum = 1;
            for (Message msg : messages) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(msg.getType().toString());
                row.createCell(1).setCellValue(msg.getUsername());
                row.createCell(2).setCellValue(msg.getTime().format(formatter));
                row.createCell(3).setCellValue(msg.getMessage());
            }

            workbook.write(outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
    }

    /**
     * Generates an Excel file containing the chat history for a specified room.
     *
     * @param roomIdDTO Contains the room ID for which the history is to be
     *                  exported.
     * @return Byte array of the Excel file.
     * @throws IOException If there is an error during file generation.
     */
    public byte[] generateChatHistoryExcel(RoomIdDTO roomIdDTO) throws IOException {
        logger.info("Generating Excel for chat history for room ID: {}", roomIdDTO.getRoomId());
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Chat History");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 創建header
            Row headerRow = sheet.createRow(0);
            String[] headerStrings = { "Type", "Username", "Time", "Message" };
            for (int i = 0; i < headerStrings.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headerStrings[i]);
            }

            // 填充data
            List<Message> messages = messageService.getMessagesByRoomId(roomIdDTO.getRoomId());
            int rowNum = 1;
            for (Message msg : messages) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(msg.getType().toString());
                row.createCell(1).setCellValue(msg.getUsername());
                row.createCell(2).setCellValue(msg.getTime().format(formatter));
                row.createCell(3).setCellValue(msg.getMessage());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Retrieves all files associated with a specific username from the RSA entity
     * records.
     *
     * @param rsaEntity An entity containing the username.
     * @return A list of RSAEntity associated with the given username.
     */
    public List<RSAEntity> selectAllFilesByUsername(RSAEntity rsaEntity) {
        List<RSAEntity> entity = rsaDAO.findByUsername(rsaEntity.getUsername());
        logger.debug("Retrieved entity: {}", entity);
        return entity;
    }
}

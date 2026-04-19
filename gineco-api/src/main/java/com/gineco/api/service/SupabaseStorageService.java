package com.gineco.api.service;

import com.gineco.api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String serviceKey;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();

    // Método genérico para subir archivos organizados por carpeta
    public String upload(MultipartFile file, String carpeta) {
        validateFile(file);
        String extension = getExtension(file.getOriginalFilename());
        String key = carpeta + "/" + UUID.randomUUID() + "." + extension;
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + key;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceKey);
        headers.setContentType(MediaType.parseMediaType(
                file.getContentType() != null ? file.getContentType() : "application/octet-stream"));

        try {
            HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return key; // Retorna la llave (ruta interna)
        } catch (IOException e) {
            throw new BusinessException("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("Error de conexión con Supabase: " + e.getMessage());
        }
    }

    // --- MÉTODOS QUE FALTABAN PARA SOLUCIONAR EL ERROR ---

    /**
     * Sube un archivo a la carpeta predeterminada 'consultas'
     * y retorna la URL pública completa.
     */
    public String uploadFile(MultipartFile file) {
        String key = upload(file, "consultas");
        return getPublicUrl(key);
    }

    /**
     * Extrae la 'key' de la URL pública y elimina el objeto de Supabase.
     */
    public void deleteFile(String urlArchivo) {
        if (urlArchivo == null || !urlArchivo.contains(bucket + "/")) return;

        // Extrae todo lo que está después del nombre del bucket en la URL
        String key = urlArchivo.substring(urlArchivo.indexOf(bucket + "/") + bucket.length() + 1);
        delete(key);
    }

    // --- MÉTODOS DE APOYO ---

    public String getPublicUrl(String key) {
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + key;
    }

    public void delete(String key) {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + key;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceKey);
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        } catch (Exception e) {
            // Log pero no lanzar error si el archivo ya no existe físicamente
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new BusinessException("El archivo está vacío");
        long maxBytes = 20L * 1024 * 1024; // 20MB
        if (file.getSize() > maxBytes) throw new BusinessException("El archivo supera el límite de 20MB");

        String ct = file.getContentType();
        if (ct == null) throw new BusinessException("Tipo de archivo no reconocido");

        if (!ct.startsWith("image/") && !ct.equals("application/pdf")) {
            throw new BusinessException("Solo se permiten imágenes (JPG, PNG) y archivos PDF");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
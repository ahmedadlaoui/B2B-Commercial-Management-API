package com.example.smartshop.controller;

import com.example.smartshop.dto.ClientCreateRequest;
import com.example.smartshop.dto.ClientDTO;
import com.example.smartshop.dto.ClientUpdateRequest;
import com.example.smartshop.service.ClientService;
import com.example.smartshop.util.AuthorizationUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final AuthorizationUtil authorizationUtil;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createClient(
            @Valid @RequestBody ClientCreateRequest request,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        ClientDTO client = clientService.createClient(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Client created successfully");
        response.put("client", client);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientUpdateRequest request,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        ClientDTO client = clientService.updateClient(id, request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Client updated successfully");
        response.put("client", client);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getClientById(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        ClientDTO client = clientService.getClientById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("client", client);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getClientByUserId(
            @PathVariable Long userId,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        ClientDTO client = clientService.getClientByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("client", client);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getClientByEmail(
            @PathVariable String email,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        ClientDTO client = clientService.getClientByEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("client", client);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClients(HttpSession session) {
        authorizationUtil.requireAdmin(session);

        List<ClientDTO> clients = clientService.getAllClients();

        Map<String, Object> response = new HashMap<>();
        response.put("clients", clients);
        response.put("count", clients.size());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteClient(
            @PathVariable Long id,
            HttpSession session) {

        authorizationUtil.requireAdmin(session);

        clientService.deleteClient(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Client deleted successfully");

        return ResponseEntity.ok(response);
    }
}

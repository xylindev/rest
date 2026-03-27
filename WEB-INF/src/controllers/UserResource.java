package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import models.dao.UserDAO;
import models.dto.User;

@WebServlet("/users/*")
public class UserResource extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, res);
        } else {
            super.service(req, res);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String format = (req.getParameter("type") == null) ? "json" : req.getParameter("type");
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.equals("/leaderboard")) {
            // -- GET /users/leaderboard --
            try {
                UserDAO dao = new UserDAO();
                try {
                    List<User> leaderboard = dao.getLeaderboard();
                    sendResponse(res, leaderboard, format);
                } finally {
                    dao.getCONNECTION().close();
                }
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération du leaderboard : " + e.getMessage());
            }
        } else {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Ressource introuvable. Essayez /users/leaderboard");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- PUT /users/id --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur requis");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.split("/")[1]);
            User updateData = objectMapper.readValue(req.getReader(), User.class);
            updateData.setId(id);

            UserDAO dao = new UserDAO();
            try {
                User existing = dao.find(id);
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur introuvable");
                    return;
                }

                dao.update(updateData);
                res.setStatus(HttpServletResponse.SC_OK);
                sendResponse(res, updateData, "json");
            } finally {
                dao.getCONNECTION().close();
            }
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- PATCH /users/id --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur requis");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.split("/")[1]);
            User patchData = objectMapper.readValue(req.getReader(), User.class);

            UserDAO dao = new UserDAO();
            try {
                User existing = dao.find(id);
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur introuvable");
                    return;
                }

                if (patchData.getLogin() != null) { existing.setLogin(patchData.getLogin()); }
                if (patchData.getPassword() != null) { existing.setPassword(patchData.getPassword()); }
                if (patchData.getRole() != null) { existing.setRole(patchData.getRole()); }

                dao.update(existing);
                res.setStatus(HttpServletResponse.SC_OK);
                sendResponse(res, existing, "json");
            } finally {
                dao.getCONNECTION().close();
            }
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void sendResponse(HttpServletResponse res, Object data, String format) throws IOException {
        PrintWriter out = res.getWriter();
        if ("json".equals(format)) {
            res.setContentType("application/json;charset=UTF-8");
            out.print(objectMapper.writeValueAsString(data));
        } else if ("xml".equals(format)) {
            res.setContentType("application/xml;charset=UTF-8");
            out.print(xmlMapper.writeValueAsString(data));
        } else {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "type non supporté : utilisez json ou xml");
        }
    }
}
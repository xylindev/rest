package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import models.dao.WasteTypeDAO;
import models.dto.WasteType;

@WebServlet("/waste-types/*")
public class WasteTypeResource extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String type = (req.getParameter("type") == null) ? "json" : req.getParameter("type");
        String pathInfo = req.getPathInfo();

        try {
            WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
            
            try {
                if (pathInfo == null || pathInfo.equals("/")) {
                    // -- GET /waste-types --
                    List<WasteType> wasteTypes = wasteTypeDAO.findAll();
                    sendResponse(res, wasteTypes, type);
                } else {
                    // -- GET /waste-types/id --
                    String[] urlTable = pathInfo.split("/");
                    if (urlTable.length >= 2) {
                        int id = Integer.parseInt(urlTable[1]);
                        WasteType wasteType = wasteTypeDAO.find(id);

                        if (wasteType == null) {
                            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Type de déchet non existant !"); // 404
                            return;
                        }
                        sendResponse(res, wasteType, type);
                    }
                }
            } finally {
                wasteTypeDAO.getCONNECTION().close();
            }
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- POST /waste-types --
        try {
            WasteType wasteType = objectMapper.readValue(req.getReader(), WasteType.class);
            WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
            
            try {
                wasteTypeDAO.insert(wasteType);
                res.setStatus(HttpServletResponse.SC_CREATED);
            } finally {
                wasteTypeDAO.getCONNECTION().close();
            }
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erreur de syntaxe JSON ou d'insertion : " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- PUT /waste-types/id --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.split("/")[1]);
            WasteType wasteType = objectMapper.readValue(req.getReader(), WasteType.class);
            wasteType.setId(id);

            WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
            try {
                boolean isUpdated = wasteTypeDAO.update(wasteType);
                if (isUpdated) {
                    res.setStatus(HttpServletResponse.SC_OK);
                } else {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Type de déchet introuvable");
                }
            } finally {
                wasteTypeDAO.getCONNECTION().close();
            }
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erreur lors de la mise à jour");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- DELETE /waste-types/id --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(pathInfo.split("/")[1]);
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'ID invalide");
            return;
        }

        try {
            WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
            try {
                wasteTypeDAO.delete(id);
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } finally {
                wasteTypeDAO.getCONNECTION().close();
            }
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                res.sendError(HttpServletResponse.SC_CONFLICT, "Conflit : ce type est utilisé dans la table des dépôts.");
            } else {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur SQL : " + e.getMessage());
            }
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
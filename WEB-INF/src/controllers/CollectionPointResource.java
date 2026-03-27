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

import models.dao.CollectionPointDAO;
import models.dto.CollectionPoint;
import models.dto.PointStatus;

@WebServlet("/points/*")
public class CollectionPointResource extends HttpServlet {

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

        try {
            CollectionPointDAO dao = new CollectionPointDAO();
            try {
                if (pathInfo == null || pathInfo.equals("/")) {
                    // -- GET /points --
                    List<CollectionPoint> points = dao.findAll();
                    sendResponse(res, points, format);
                    return;
                } 

                String[] urlTable = pathInfo.split("/");

                // -- GET /points/overloaded --
                if (urlTable.length == 2 && urlTable[1].equals("overloaded")) {
                    List<PointStatus> overloadedList = dao.getOverloadedPoints();
                    sendResponse(res, overloadedList, format);
                    return;
                }

                // -- GET /points/id/status --
                if (urlTable.length == 3 && urlTable[2].equals("status")) {
                    int id = Integer.parseInt(urlTable[1]);
                    PointStatus status = dao.getPointStatus(id);
                    if (status == null) {
                        res.sendError(HttpServletResponse.SC_NOT_FOUND, "Point de collecte introuvable !");
                        return;
                    }
                    sendResponse(res, status, format);
                    return;
                }

                // -- GET /points/id --
                if (urlTable.length == 2 && !urlTable[1].equals("overloaded")) {
                    int id = Integer.parseInt(urlTable[1]);
                    CollectionPoint point = dao.find(id);
                    if (point == null) {
                        res.sendError(HttpServletResponse.SC_NOT_FOUND, "ID non existant !");
                        return;
                    }
                    sendResponse(res, point, format);
                }

            } finally {
                dao.getCONNECTION().close();
            }
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- PUT /points/id --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID requis");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.split("/")[1]);
            CollectionPoint patch = objectMapper.readValue(req.getReader(), CollectionPoint.class);

            CollectionPointDAO dao = new CollectionPointDAO();
            try {
                CollectionPoint existing = dao.find(id);
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "ID non existant !");
                    return;
                }

                existing.setAdresse(patch.getAdresse());
                existing.setCapaciteMax(patch.getCapaciteMax());
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

    protected void doPatch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- PATCH /points/id --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID requis");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.split("/")[1]);
            CollectionPoint patch = objectMapper.readValue(req.getReader(), CollectionPoint.class);

            CollectionPointDAO dao = new CollectionPointDAO();
            try {
                CollectionPoint existing = dao.find(id);
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "ID non existant !");
                    return;
                }

                if (patch.getAdresse() != null) {
                    existing.setAdresse(patch.getAdresse());
                    dao.patchAdresse(id, patch.getAdresse());
                }

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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- DELETE /points/id/clear --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 3 || !pathInfo.split("/")[2].equals("clear")) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Utilisation : DELETE /points/id/clear");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.split("/")[1]);

            CollectionPointDAO dao = new CollectionPointDAO();
            try {
                CollectionPoint existing = dao.find(id);
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "ID non existant !");
                    return;
                }

                dao.clearDeposits(id);
                res.setStatus(HttpServletResponse.SC_NO_CONTENT); 
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
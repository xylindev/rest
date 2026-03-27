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

@WebServlet("/points/*")
public class CollectionPointResource extends HttpServlet {

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
        PrintWriter out = res.getWriter();
        String parameter = req.getParameter("type");
        String pathInfo = req.getPathInfo();

        ObjectMapper objectMapper = new ObjectMapper();
        XmlMapper xmlMapper = new XmlMapper();

        if (pathInfo == null || pathInfo.equals("/")) {
            // -- GET /points --
            try {
                CollectionPointDAO dao = new CollectionPointDAO();
                List<CollectionPoint> points = dao.findAll();
                dao.getCONNECTION().close();

                if (parameter == null || parameter.equals("json")) {
                    res.setContentType("application/json;charset=UTF-8");
                    out.print(objectMapper.writeValueAsString(points));
                } else if (parameter.equals("xml")) {
                    res.setContentType("application/xml;charset=UTF-8");
                    out.print(xmlMapper.writeValueAsString(points));
                } else {
                    res.sendError(400, "type : json | xml");
                }
            } catch (Exception e) {
                out.print(e.getMessage());
            }
        } else {
            // -- GET /points/id --
            String[] urlTable = pathInfo.split("/");
            try {
                CollectionPointDAO dao = new CollectionPointDAO();
                int id = Integer.parseInt(urlTable[1]);
                CollectionPoint point = dao.find(id);
                dao.getCONNECTION().close();

                if (point == null) {
                    res.sendError(404, "ID non existant !");
                    return;
                }

                if (parameter == null || parameter.equals("json")) {
                    res.setContentType("application/json;charset=UTF-8");
                    out.print(objectMapper.writeValueAsString(point));
                } else if (parameter.equals("xml")) {
                    res.setContentType("application/xml;charset=UTF-8");
                    out.print(xmlMapper.writeValueAsString(point));
                } else {
                    res.sendError(400, "type : json | xml");
                }
            } catch (NumberFormatException e) {
                res.sendError(400, "ID invalide");
            } catch (Exception e) {
                out.print(e.getMessage());
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- PUT /points/id --
        PrintWriter out = res.getWriter();
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            res.sendError(400, "ID requis");
            return;
        }

        try {
            String[] urlTable = pathInfo.split("/");
            int id = Integer.parseInt(urlTable[1]);

            ObjectMapper objectMapper = new ObjectMapper();
            CollectionPoint patch = objectMapper.readValue(req.getReader(), CollectionPoint.class);

            CollectionPointDAO dao = new CollectionPointDAO();
            CollectionPoint existing = dao.find(id);

            if (existing == null) {
                res.sendError(404, "ID non existant !");
                dao.getCONNECTION().close();
                return;
            }

            existing.setAdresse(patch.getAdresse());
            existing.setCapaciteMax(patch.getCapaciteMax());
            dao.update(existing);
            dao.getCONNECTION().close();

            res.setStatus(200);
            res.setContentType("application/json;charset=UTF-8");
            out.print(objectMapper.writeValueAsString(existing));
        } catch (NumberFormatException e) {
            res.sendError(400, "ID invalide");
        } catch (Exception e) {
            out.print(e.getMessage());
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- PATCH /points/id --
        PrintWriter out = res.getWriter();
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            res.sendError(400, "ID requis");
            return;
        }

        try {
            String[] urlTable = pathInfo.split("/");
            int id = Integer.parseInt(urlTable[1]);

            ObjectMapper objectMapper = new ObjectMapper();
            CollectionPoint patch = objectMapper.readValue(req.getReader(), CollectionPoint.class);

            CollectionPointDAO dao = new CollectionPointDAO();
            CollectionPoint existing = dao.find(id);

            if (existing == null) {
                res.sendError(404, "ID non existant !");
                dao.getCONNECTION().close();
                return;
            }

            if (patch.getAdresse() != null) {
                dao.patchAdresse(id, patch.getAdresse());
                existing.setAdresse(patch.getAdresse());
            }
            dao.getCONNECTION().close();

            res.setStatus(200);
            res.setContentType("application/json;charset=UTF-8");
            out.print(objectMapper.writeValueAsString(existing));
        } catch (NumberFormatException e) {
            res.sendError(400, "ID invalide");
        } catch (Exception e) {
            out.print(e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- DELETE /points/id/clear --
        PrintWriter out = res.getWriter();
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            res.sendError(400, "Chemin invalide");
            return;
        }

        String[] urlTable = pathInfo.split("/");

        if (urlTable.length < 3 || !urlTable[2].equals("clear")) {
            res.sendError(400, "Utilisation : DELETE /points/id/clear");
            return;
        }

        try {
            int id = Integer.parseInt(urlTable[1]);

            CollectionPointDAO dao = new CollectionPointDAO();
            CollectionPoint existing = dao.find(id);

            if (existing == null) {
                res.sendError(404, "ID non existant !");
                dao.getCONNECTION().close();
                return;
            }

            dao.clearDeposits(id);
            dao.getCONNECTION().close();

            res.setStatus(200);
            out.print("Dépôts du point " + id + " vidés.");
        } catch (NumberFormatException e) {
            res.sendError(400, "ID invalide");
        } catch (Exception e) {
            out.print(e.getMessage());
        }
    }
}

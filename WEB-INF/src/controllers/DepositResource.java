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

import models.dao.DepositDAO;
import models.dto.Deposit;
import models.dto.DepositDetail;

@WebServlet("/deposits/*")
public class DepositResource extends HttpServlet {

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
            DepositDAO dao = new DepositDAO();
            try {
                if (pathInfo == null || pathInfo.equals("/")) {
                    // -- GET /deposits --
                    List<DepositDetail> deposits = dao.findAllDetails();
                    sendResponse(res, deposits, format);
                    return;
                }

                // -- GET /deposits/id --
                String[] urlTable = pathInfo.split("/");
                if (urlTable.length == 2) {
                    int id = Integer.parseInt(urlTable[1]);
                    Deposit deposit = dao.find(id);
                    if (deposit == null) {
                        res.sendError(HttpServletResponse.SC_NOT_FOUND, "Dépôt introuvable !");
                        return;
                    }
                    sendResponse(res, deposit, format);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- POST /deposits --
        try {
            Deposit deposit = objectMapper.readValue(req.getReader(), Deposit.class);

            if (deposit.getPoids() <= 0) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Le poids doit être positif.");
                return;
            }

            DepositDAO dao = new DepositDAO();
            try {
                if (dao.isSaturated(deposit.getPointId(), deposit.getPoids())) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Point de collecte saturé.");
                    return;
                }
                dao.insert(deposit);
                res.setStatus(HttpServletResponse.SC_CREATED);
            } finally {
                dao.getCONNECTION().close();
            }
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // -- PUT /deposits/id --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID requis");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.split("/")[1]);
            Deposit updateData = objectMapper.readValue(req.getReader(), Deposit.class);

            DepositDAO dao = new DepositDAO();
            try {
                Deposit existing = dao.find(id);
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Dépôt introuvable !");
                    return;
                }

                existing.setUserId(updateData.getUserId());
                existing.setPointId(updateData.getPointId());
                existing.setWasteTypeId(updateData.getWasteTypeId());
                existing.setPoids(updateData.getPoids());
                existing.setCollecte(updateData.isCollecte());

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
        // -- PATCH /deposits/id --
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID requis");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.split("/")[1]);
            Deposit patchData = objectMapper.readValue(req.getReader(), Deposit.class);

            DepositDAO dao = new DepositDAO();
            try {
                Deposit existing = dao.find(id);
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Dépôt introuvable !");
                    return;
                }

                if (patchData.getPoids() > 0)        { existing.setPoids(patchData.getPoids()); }
                if (patchData.getUserId() != 0)      { existing.setUserId(patchData.getUserId()); }
                if (patchData.getPointId() != 0)     { existing.setPointId(patchData.getPointId()); }
                if (patchData.getWasteTypeId() != 0) { existing.setWasteTypeId(patchData.getWasteTypeId()); }

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

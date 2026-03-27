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

@WebServlet("/deposits/*")
public class DepositResource extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String format = (req.getParameter("type") == null) ? "json" : req.getParameter("type");
        String pathInfo = req.getPathInfo();

        try {
            DepositDAO dao = new DepositDAO();
            try {
                if (pathInfo == null || pathInfo.equals("/")) {
                    // -- GET /deposits --
                    List<Deposit> deposits = dao.findAll();
                    sendResponse(res, deposits, format);
                } else {
                    // -- GET /deposits/id --
                    String[] urlTable = pathInfo.split("/");
                    if (urlTable.length >= 2) {
                        int id = Integer.parseInt(urlTable[1]);
                        Deposit deposit = dao.find(id);

                        if (deposit == null) {
                            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Dépôt non existant !");
                            return;
                        }
                        sendResponse(res, deposit, format);
                    }
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
            if (deposit.getPoids() < 0) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Le poids du dépôt ne peut pas être négatif.");
                return;
            }

            DepositDAO dao = new DepositDAO();
            try {
                if (dao.isSaturated(deposit.getPointId(), deposit.getPoids())) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Dépôt refusé : Le point de collecte est saturé.");
                    return;
                }

                boolean isInserted = dao.insert(deposit);
                if (isInserted) {
                    res.setStatus(HttpServletResponse.SC_CREATED);
                    sendResponse(res, deposit, "json");
                } else {
                    res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de l'insertion.");
                }

            } finally {
                dao.getCONNECTION().close();
            }
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erreur de format JSON ou de données : " + e.getMessage());
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
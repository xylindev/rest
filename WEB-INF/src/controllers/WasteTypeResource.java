package controllers;

import java.io.BufferedReader;
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

import models.dao.WasteTypeDAO;
import models.dto.WasteType;

@WebServlet("/waste-type/*")
public class WasteTypeResource extends HttpServlet{
// -----------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String type = (req.getParameter("type") == null) ? "json" : req.getParameter("type");


        ObjectMapper objectMapper = new ObjectMapper();
        XmlMapper xmlMapper = new XmlMapper();

        // -- GET /waste-types --
        try {
            WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
            List<WasteType> wasteTypes = wasteTypeDAO.findAll();

            if(type.equals("json")){
                res.setContentType("application/json;charset=UTF-8");
                String json = objectMapper.writeValueAsString(wasteTypes);
                out.print(json);
            } else if(type.equals("xml")) {
                res.setContentType("application/xml;charset=UTF-8");
                String xml = xmlMapper.writeValueAsString(wasteTypes);
                out.print(xml);
            } else {
                res.sendError(400, "type : json | xml");
            }
            wasteTypeDAO.getCONNECTION().close();
        } catch (Exception e) {
            out.print(e.getMessage());
        }

        // -- GET /waste-types/id --
        String pathInfo = req.getPathInfo();
        String[] urlTable;

        if(pathInfo != null) {
            urlTable = pathInfo.split("/");

            if(urlTable.length >= 2) {
                res.resetBuffer();

                try {
                    WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
                    int id = Integer.parseInt(urlTable[1]);
        
                    WasteType wasteType = wasteTypeDAO.find(id);
        
                    if(wasteType == null) {
                        res.sendError(404, "ID non existant !");
                        return;
                    }
        
                    if(type.equals("json")){
                        res.setContentType("application/json;charset=UTF-8");
                        String json = objectMapper.writeValueAsString(wasteType);
                        out.print(json);
                    } else if(type.equals("xml")) {
                        res.setContentType("application/xml;charset=UTF-8");
                        String xml = xmlMapper.writeValueAsString(wasteType);
                        out.print(xml);
                    } else {
                        res.sendError(400, "type : json | xml");
                    }
                    
                    wasteTypeDAO.getCONNECTION().close();
                } catch (Exception e) {
                    out.print(e.getMessage());
                }
            }
        }
    }

// -----------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();

        int id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("name");
        int pointsPerKilo = Integer.parseInt(req.getParameter("pointsPerKilo"));

        WasteType wasteType = new WasteType(id, name, pointsPerKilo);

        // -- POST /waste-types --
        try {
            WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
            wasteTypeDAO.insert(wasteType);
            wasteTypeDAO.getCONNECTION().close();
        } catch (Exception e) {
            out.print(e.getMessage());
        }
    }

// -----------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String[] urlTable = req.getPathInfo().split("/");

        BufferedReader reader = req.getReader();
        String data = reader.readLine();

        if(urlTable.length >= 2) {
            ObjectMapper objectMapper = new ObjectMapper();

            int id = Integer.parseInt(urlTable[1]);
            WasteType wasteType = objectMapper.readValue(data, WasteType.class);
            wasteType.setId(id);

            // -- PUT /waste-types/id --
            try {
                WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
                wasteTypeDAO.update(wasteType);
                wasteTypeDAO.getCONNECTION().close();
            } catch (Exception e) {
                out.print(e.getMessage());
            }
        }
    }

// -----------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();

        String[] urlTable = req.getPathInfo().split("/");

        if(urlTable.length >= 2) {
            int id = Integer.parseInt(urlTable[1]);

            // -- DELETE /waste-types/id --
            try {
                WasteTypeDAO wasteTypeDAO = new WasteTypeDAO();
                wasteTypeDAO.delete(id);
                wasteTypeDAO.getCONNECTION().close();
            } catch (Exception e) {
                out.print(e.getMessage());
            }
        }
    }

// -----------------------------------------------------------------------------------------------------------------------------------
}

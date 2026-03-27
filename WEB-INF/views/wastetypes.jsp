<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ page import="dto.WasteTypeDTO, java.util.List" %>

<!DOCTYPE html>
<html lang="fr">

<head>
    <title>EcoDrop - Catalogue des Déchets</title>
    <style>
        table { width: 60%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }
        th { background-color: #f4f4f4; }
    </style>
</head>

<body>
    <h2>Liste des types de déchets</h2>
    <table>
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Points/Kg</th>
        </tr>
        <% 
            List<WasteTypeDTO> list = (List<WasteTypeDTO>) request.getAttribute("wasteTypes");
            
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    WasteTypeDTO waste = list.get(i);
        %>
                <tr>
                    <td><%= waste.getID() %></td>
                    <td><%= waste.getName() %></td>
                    <td><%= waste.getPointsPerKilo() %></td>
                </tr>
        <% 
                } 
            } else { 
        %>
            <tr><td colspan="3">Aucun déchet trouvé.</td></tr>
        <% } %>
    </table>
</body>
</html>
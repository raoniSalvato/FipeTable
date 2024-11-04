package com.example.FipeTable.main;

import com.example.FipeTable.model.Data;
import com.example.FipeTable.model.Models;
import com.example.FipeTable.model.Vehicle;
import com.example.FipeTable.service.ConsumeApi;
import com.example.FipeTable.service.ConvertData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private Scanner scanner = new Scanner(System.in);
    private final String basicAddress = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumeApi consumeApi = new ConsumeApi();
    private ConvertData convertData = new ConvertData();


    public void menu(){
        var menu = """
                *** Opcoes ***
                Carro 
                Moto
                Caminhao 
                Digite uma das opcoes para consulta: 
                """;

        System.out.println(menu);
        var option = scanner.nextLine();
        String link;

        if(option.toLowerCase().contains("carr")){
            link = basicAddress + "carros/marcas";

        }else if(option.toLowerCase().contains("mot")){
            link = basicAddress + "motos/marcas";
        }else {
            link = basicAddress + "caminhoes/marcas";
        }

        var json = consumeApi.getData(link);
        System.out.println(json);
        var brands = convertData.getList(json, Data.class);
        brands.stream()
                .sorted(Comparator.comparing(Data::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta:");
        var brandCode = scanner.nextLine();

        link = link + "/" + brandCode + "/modelos/";
        json = consumeApi.getData(link);
        var modelList = convertData.getData(json, Models.class);

        System.out.println("Modelos dessa marca: \n");
        modelList.modelos().stream()
                .sorted(Comparator.comparing(Data::codigo))
                .forEach(System.out::println);

        System.out.println("Digite um trecho do nome do carro a ser buscado: \n");
        var vehicleName = scanner.nextLine();

        List<Data> filteredModels = modelList.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(vehicleName.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("Modelos filtrados: \n");
        filteredModels.forEach(System.out::println);

        System.out.println("Digite o código do modelo para buscar os valores de avaliação: ");
        var modelCode = scanner.nextLine();

        link = link + modelCode + "/anos";
        json = consumeApi.getData(link);

        List<Data> yearsList = convertData.getList(json, Data.class);

        List<Vehicle> vehicles = new ArrayList<>();

        for(int i =0; i< yearsList.size(); i++){
            var linkYear = link + "/" + yearsList.get(i).codigo();
            json = consumeApi.getData(linkYear);
            Vehicle vehicle = convertData.getData(json, Vehicle.class);
            vehicles.add(vehicle);
        }

        System.out.println("Todos os veiculos filtrados com avaliações por ano: ");
        vehicles.forEach(System.out::println);
    }
}

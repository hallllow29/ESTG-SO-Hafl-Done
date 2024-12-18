package Core;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import lib.lists.ArrayUnorderedList;

import java.io.FileWriter;

public class SaveToJson {

    private static final String FILENAME = "data.json";

    public static void saveSystem(ArrayUnorderedList<Task> tasks) {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            JSONObject  taskObject = new JSONObject();
            taskObject.put("name", task.getName());
            taskObject.put("priority", task.getPriority().toString());
            taskObject.put("duration", task.getDuration());
            taskObject.put("memorySize", task.getMemorySize());
            jsonArray.add(taskObject);
        }

        try (FileWriter file = new FileWriter(FILENAME)) {
            file.write(jsonArray.toJSONString());
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

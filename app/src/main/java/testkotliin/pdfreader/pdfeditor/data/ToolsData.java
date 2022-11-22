package testkotliin.pdfreader.pdfeditor.data;

import android.app.Activity;
import android.content.Context;
import testkotliin.pdfreader.pdfeditor.R;
import testkotliin.pdfreader.pdfeditor.GetSet.Tool;

import java.util.ArrayList;
import java.util.List;

public class ToolsData {

    public static List<Tool> getTools(Context context) {
        ArrayList<Tool> arrayList = new ArrayList<>();
        arrayList.add(new Tool(1, context.getString(R.string.merge),  R.drawable.ic_tool_merge));
        arrayList.add(new Tool(2, context.getString(R.string.split), R.drawable.ic_tool_split));
        arrayList.add(new Tool(3, context.getString(R.string.extract_images),  R.drawable.ic_tool_library));
        arrayList.add(new Tool(4, context.getString(R.string.save_as_pictures),  R.drawable.ic_tool_photo));
        arrayList.add(new Tool(5, context.getString(R.string.organize_pages),  R.drawable.ic_tool_headline));
        arrayList.add(new Tool(6, context.getString(R.string.edit_metadata),  R.drawable.ic_tool_metadata));
        arrayList.add(new Tool(7, context.getString(R.string.compress),  R.drawable.ic_tool_compress));
        arrayList.add(new Tool(8, context.getString(R.string.extract_text),  R.drawable.ic_tool_text));
        arrayList.add(new Tool(9, context.getString(R.string.images_to_pdf),  R.drawable.ic_tool_pdf));
        context.getString(R.string.protect);
        context.getString(R.string.unprotect);
        context.getString(R.string.stamp);
        return arrayList;
    }
}

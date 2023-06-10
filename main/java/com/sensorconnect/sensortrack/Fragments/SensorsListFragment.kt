package com.sensorconnect.sensortrack.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R


class SensorsListFragment : Fragment() {

    private var context : Context? = null
    private var view : View? = null
    private var recyclerview: RecyclerView? = null
    private var adapter: SensorListAdapter? = null
    private val title_sensors = arrayOf(
        "Aim",//1
        "SOS Service",//2
        "Step Counter",//3
        "Ball Game",//4
        "Compass",//5
        "Light Sensor",//6
        "Heart Rate",//7
        "Find Metal",//8
        "Instructions",//9
        "Warnings",//10
        "About Developer",//11
        "Contact",//12
    )
    private val description_sensors = arrayOf(
        /*1*/"Foster a sense of curiosity and exploration by offering a range of sensor-based features that allow users to interact with their surroundings in unique and innovative ways.",
        /*2*/"Prioritize user safety and provide a reliable safety net during critical situations, " +
                "offering users a sense of security and reassurance in their day-to-day lives." +
                "\n-SOS Button with alert sound",
        /*3*/"Uses TYPE_STEP sensor of mobile device.\n- We can put target kilometers to run." +
                "\n- It'll count steps and notify when we reached target.",
        /*4*/"Ball rotates based on phone rotation. This sensor used during gaming like driving vehicles",
        /*5*/"Provides a reliable and portable tool for determining direction accurately, " +
                "helping users navigate and orient themselves in various environments." +
                "\n-Used during sailing\n-In forest travelling\n- Astronomy\n- Education and Learning\n- Surveying and Mapping",
        /*6*/"Calculates light intensity using TYPE_LIGHT sensor.\n- Give suggestions to optimize electrical light sources and for home health",
        /*7*/"Available only in few devices especially in samsung mobile phones. Calculates approximate Heart Rate count",
        /*8*/"Scans surface and gets threshold of mgnetic field using TYPE_MAGNETIC_FIELD sensor.\n- Give approximate existance of metals based on threshold",
        /*9*/"- Reset buttons used to delete calculating data. ",


        /*10*/"* Heart Rate sensor works only for few mobile phones" +
                "\n* Even 1 permission not accepted, homepage buttons won't work." +
                "\n* Even if you keep 'Ask every time', buttons won't work",

        /*11*/"Done by MADDIKERI NARENDRANATH REDDY from VIT-AP University",
        /*12*/"Mail to \ndossier.technologies15@gmail.com\n for suggestions and developer contact"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_sensors_list, container, false)
        //------------------------------
        recyclerview = view?.findViewById(R.id.recyclerview_fragment_sensors_list)
        //------------------
        recyclerview?.layoutManager = LinearLayoutManager(context)
        adapter = SensorListAdapter(title_sensors, description_sensors, this.context)
        recyclerview?.adapter = adapter
        //------------------------------
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentsCallback) {
            this.context = context.getContext()
        } else {
            throw IllegalArgumentException("Parent activity must implement FragmentsCallback")
        }
    }
}

class SensorListAdapter(titleSensors: Array<String>, descriptionSensors: Array<String>, context: Context?) : RecyclerView.Adapter<SensorListAdapter.ViewHolder>(){

    private val title_sensor_array : Array<String> = titleSensors
    private val description_sensor_array : Array<String> = descriptionSensors
    private val context : Context? = context



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(this.context).inflate(R.layout.sensorslist_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.title_sensor_array.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = holder.adapterPosition
        holder.title_sensor_tv.text = "${this.title_sensor_array.get(pos)}"
        holder.desciption_sensor_tv.text = "${this.description_sensor_array.get(pos)}"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title_sensor_tv: TextView = itemView.findViewById(R.id.sensor_title_sensorlist_item_tv)
        val desciption_sensor_tv: TextView = itemView.findViewById(R.id.sensor_description_sensorlist_item_tv)
    }






}

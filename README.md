<a name = "readme-top"></a>

<!-- Table of Contents -->
<details>
    <summary>Table of Contents</summary>
    <ol>
        <li><a href = "#about-the-project">About the Project</a></li>
        <li><a href = "#built-with">Built With</a></li>
        <li><a href = "#how-to-use">How To Use</a></li>
        <li><a href = "#contact">Contact</a></li>
    </ol>
</details>

# About the Project
Name: COVID Data System <br>
Started: 8/07/24 <br>
Description: a multi-tiered application to process and visualize COVID-19 datasets, giving insight on population impacts across regions <br>

# Built With
<a href="https://java.com/en/">
    <img width="37" src="https://github.com/user-attachments/assets/b563b96c-8d38-4192-8d9b-be10236394ff" />
</a>

# How To Use
### 1. Gather datasets
There are three datasets needed for this program. <br>
a. COVID data (stored in a .CSV or .JSON file type) <br>
![Screenshot 2024-09-07 at 12 43 14 PM](https://github.com/user-attachments/assets/bbfc7196-3d2d-4ecc-b64d-03e8219db93c)


b. Population data (stored in a .CSV file type) <br>
![Screenshot 2024-09-07 at 12 46 10 PM](https://github.com/user-attachments/assets/28285596-dba9-450e-8176-cb2823733ea5)


c. Properties data (stored in a .CSV file type) <br>
![Screenshot 2024-09-07 at 12 48 07 PM](https://github.com/user-attachments/assets/c73c955e-5d13-495e-a043-7b9b1a58af67)


Example datasets gathered from OpenDataPhilly are here for download in this repository:

![Screenshot 2024-09-07 at 2 42 54 AM copy](https://github.com/user-attachments/assets/527b286f-410e-405d-a751-127fb53617e1)


<br>

### 2. Download 'coviddatasystem' package from the repository
![Screenshot 2024-09-07 at 2 42 54 AM](https://github.com/user-attachments/assets/310a13ca-d1e8-45e4-bdb2-ae4305941a17) <br>

Import into your local IDE as a Java Project. The file tree should look like this: <br>


<img src="https://github.com/user-attachments/assets/cf4edad6-87e7-4245-a00a-02936647b9b2" width="400" />


<br>

### 3. Set up run-time arguments
Every IDE is slightly different in setting up run-time arguments (look up a tutorial for your specific IDE if needed) <br>
The four required run-time arguments are:

![Screenshot 2024-09-07 at 2 55 39 AM](https://github.com/user-attachments/assets/83ff8280-2f0f-469d-82b6-2179a72f48c6)


<br>

### 4. Add referenced library
Download the file "json-simple-1.1.1.jar" from this repository and add it as a Referenced Library in your Java project

![Screenshot 2024-09-07 at 2 42 54 AM](https://github.com/user-attachments/assets/70999a3a-548c-4fda-b577-8e8e3b205e5f)



<br>

### 5. Run the program
If you input your run-time arguments incorrectly, you will receive an alert. <br>
Below, the key word used before my desired log file output was invalid. It should have been "--log=log.txt".

![Screenshot 2024-09-07 at 2 05 31 AM](https://github.com/user-attachments/assets/1eace145-eff1-4a6f-8c5c-c79631f29a1d)

If your data sets and run-time arguments were parsed correctly, you should see this menu:

![Screenshot 2024-09-07 at 2 49 42 AM](https://github.com/user-attachments/assets/5c869052-22dc-48b1-87cc-eb8a9a38391e)

Now you can choose a menu option to manipulate your datasets and receive outputs. <br>
Example, option 2:
![Screenshot 2024-09-07 at 3 22 20 AM](https://github.com/user-attachments/assets/f3347e72-5c89-43b6-af40-0dab16e902df)

Example, option 7:
![Screenshot 2024-09-07 at 3 26 19 AM](https://github.com/user-attachments/assets/28f4ee2d-d817-4b51-b7ba-62e652866597)


<br>

### 6. Log file will be generated
After running the program, your log file will be created and updated with your run-time arguments, opened files via the CSV/JSON parsers to populate our data trees, user inputs, and time stamps:

![Screenshot 2024-09-07 at 3 29 26 AM](https://github.com/user-attachments/assets/6750c934-aab2-4868-a6e1-a5800210d430)
Note: Timestamps are in Unix epoch time, the number of milliseconds that have elapsed since January 1, 1970, 00:00:00 UTC.


# Contact
Feel free to leave suggestions or ask questions about any part of the project! <br>
Kai Fang - (haifromkai.tech@gmail.com) <br>
Project Link: https://github.com/haifromkai/COVID-Data-System


<p align = "right">(<a href = "#readme-top">back to top</a>)</p>

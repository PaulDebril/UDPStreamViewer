# Video Frame Extractor using OpenCV

This Python script allows you to extract frames from a video file at a specific frame rate (FPS) using OpenCV. It's useful for generating image datasets from videos for machine learning, computer vision, or other purposes.

## Features
- Extract frames from any video format supported by OpenCV.
- Control the extraction rate by specifying the desired FPS (frames per second).
- Save the extracted frames as image files in a specified directory.

## Requirements

- Python 3.x
- OpenCV (`cv2` library)

You can install the required library using `pip`:

```bash
pip install opencv-python
```

## How to Use

1. Place your video file in the desired directory.
2. Edit the script to specify the path to your video file and the desired output folder for the extracted frames.
3. Run the script, and it will extract frames at the specified FPS.

## Script

Below is the Python code you can use to extract frames from a video.

```python
import cv2
import os

# Path to the video file
video_path = "path/to/video.mp4"  # Replace with your video file path
vidcap = cv2.VideoCapture(video_path)

# Get the FPS of the video
fps = vidcap.get(cv2.CAP_PROP_FPS)
print(f"FPS of the video: {fps}")

# Set the desired FPS (for example, extract 1 frame per second)
desired_fps = 25  # Change this number based on your needs

# Calculate the number of frames to skip between each extraction
frames_to_skip = int(fps / desired_fps)

# Create a folder to save the extracted frames
output_folder = "extracted_frames"
if not os.path.exists(output_folder):
    os.makedirs(output_folder)

# Initialize frame reading
success, image = vidcap.read()
count = 0
frame_number = 0

while success:
    if frame_number % frames_to_skip == 0:
        # Save each frame as an image
        frame_path = os.path.join(output_folder, f"frame_{count}.jpg")
        cv2.imwrite(frame_path, image)
        print(f"Saved: {frame_path}")
        count += 1

    # Read the next frame
    success, image = vidcap.read()
    frame_number += 1

print(f"Extraction complete. {count} frames were extracted.")
```

### Parameters

- **`video_path`**: Specify the path to your video file.
- **`desired_fps`**: Set how many frames per second you want to extract.
- **`output_folder`**: The folder where extracted frames will be saved.

### Example

If your video is recorded at 30 FPS and you set `desired_fps = 1`, the script will extract one frame every second of the video.


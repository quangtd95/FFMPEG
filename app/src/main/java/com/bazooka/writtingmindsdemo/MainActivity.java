package com.bazooka.writtingmindsdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.sql.Time;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TAGG";

    FFmpeg ffmpeg;

    EditText commandEditText;

    LinearLayout outputLayout;

    Button runButton;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ffmpeg = FFmpeg.getInstance(this);
        commandEditText = (EditText) findViewById(R.id.command);
        outputLayout = (LinearLayout) findViewById(R.id.command_output);
        runButton = (Button) findViewById(R.id.run_command);

        loadFFMpegBinary();
        initUI();
    }

    private void initUI() {
        runButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void execFFmpegBinary(final String[] command, final String[] commandNext) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    addTextViewToLayout("FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) throws FFmpegCommandAlreadyRunningException {
                    addTextViewToLayout("SUCCESS with output : " + s);
                    ffmpeg.execute(commandNext, new FFmpegExecuteResponseHandler() {
                        @Override public void onSuccess(String message) {

                        }

                        @Override public void onProgress(String message) {
                            Log.d(TAG, "Started command : ffmpeg " + commandNext);
                            addTextViewToLayout("progress : " + message);
                            progressDialog.setMessage("Processing\n" + message);
                        }

                        @Override public void onFailure(String message) {
                            addTextViewToLayout("FAILED with output : " + message);
                        }

                        @Override public void onStart() {
                            outputLayout.removeAllViews();

                            Log.d(TAG, "Started command : ffmpeg " + commandNext);
                            progressDialog.setMessage("Processing...");
                            progressDialog.show();
                        }

                        @Override public void onFinish() {
                            Log.d(TAG, "Finished command : ffmpeg " + commandNext);
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    addTextViewToLayout("progress : " + s);
                    progressDialog.setMessage("Processing\n" + s);
                }

                @Override
                public void onStart() {
                    outputLayout.removeAllViews();

                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    addTextViewToLayout("FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) throws FFmpegCommandAlreadyRunningException {
                    addTextViewToLayout("SUCCESS with output : " + s);

                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
//                    addTextViewToLayout("progress : " + s);
                    progressDialog.setMessage("Processing\n" + s);
                }

                @Override
                public void onStart() {
                    outputLayout.removeAllViews();

                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void addTextViewToLayout(String text) {
        TextView textView = new TextView(MainActivity.this);
        textView.setText(text);
        outputLayout.addView(textView);
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_command:
//                String cmd = commandEditText.getText().toString();


                String cmd = "-loop 1 -t 1 -i /storage/emulated/0/videokit/img001.png\n" +
                        "-loop 1 -t 1 -i /storage/emulated/0/sdcard/videokit/img002.png\n" +
                        "-loop 1 -t 1 -i /storage/emulated/0/sdcard/videokit/img003.png\n" +
                        "-loop 1 -t 1 -i /storage/emulated/0/sdcard/videokit/img004.png\n" +
                        "-loop 1 -t 1 -i /storage/emulated/0/sdcard/videokit/img005.png\n" +
                        "-filter_complex\n" +
                        "\"[1:v][0:v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b1v];\n" +
                        " [2:v][1:v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b2v];\n" +
                        " [3:v][2:v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b3v];\n" +
                        " [4:v][3:v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b4v];\n" +
                        " [0:v][b1v][1:v][b2v][2:v][b3v][3:v][b4v][4:v]concat=n=9:v=1:a=0,format=yuv420p[v]\" -map \"[v]\" /sdcard/videokit/imgout.mp4";

                cmd = "-loop -vframes 14490 -i /sdcard/videokit/img001.png -i /sdcard/videokit/wing.mp3 -y -r 30 \n" +
                        "    -b 2500k -acodec ac3 -ab 384k -vcodec mpeg4 /sdcard/videokit/result.mp4";

                cmd = "-i /sdcard/videokit/in.mp4 -i /sdcard/videokit/wing.mp3 -c:v copy -c:a aac -strict experimental /sdcard/videokit/output.mp4";

                cmd = "-f concat -safe 0 -i /sdcard/videokit/input.txt -vsync vfr -pix_fmt yuv420p /sdcard/videokit/output" + System.currentTimeMillis() + ".mp4";

                cmd = "-i /sdcard/videokit/img%03d.png /sdcard/videokit/output" + System.currentTimeMillis() + ".mp4";

                cmd = "-i /sdcard/videokit/in.mp4 -filter_complex blend=all_expr='if(eq(mod(X,2),mod(Y,2)),A,B)' -preset ultrafast /sdcard/videokit/outEffect.mp4";

                cmd = "ffmpeg -i /sdcard/videokit/in.mp4 -f lavfi -i color=red:s=1280x720" +
                        " -filter_complex [0:v]setsar=sar=1/1[s];" +
                        " [s][1:v]blend=shortest=1:all_mode=screen:all_opacity=0.7[out]" +
                        " -map [out] -map 0:a /sdcard/videokit/output.mp4";

                cmd = "-y -r 1/5 -f concat -safe 0 -i /sdcard/videokit/input.txt -c:v libx264 -vf fps=25,format=yuv420p /sdcard/videokit/outputhihi.mp4";

                //scale images.
                //code chạy được
                cmd = "-i /sdcard/videokit/img001.png -vf scale=iw/2:-1 /sdcard/videokit/img0011.png";
                //code không chạy được
                cmd = "-f lavfi -i testsrc=duration=10:size=1280x720:rate=30 /sdcard/videokit/img001.png";

                //code không chạy được
                cmd = "-loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img001.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img002.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img003.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img004.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img005.png" +
                        " -filter_complex" +
                        " [0:v]fade=t=out:st=4:d=1[v0];" +
                        " [1:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v1];" +
                        " [2:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v2];" +
                        " [3:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v3];" +
                        " [4:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v4];" +
                        " [v0][v1][v2][v3][v4]concat=n=5:v=1:a=0,format=yuv420p[v] -map [v] /storage/emulated/0/PICTURES/a/outconcat.mp4";


                //add audio to video
                //lỗi có tiếng mà ko có hình
                cmd = "-framerate 1/10 -i /storage/emulated/0/PICTURES/a/img%03d.png -i /storage/emulated/0/PICTURES/a/wing.mp3 -c:v libx264 -c:a aac -b:a 192k -shortest /storage/emulated/0/PICTURES/a/audiowithimage.mp4";

                //add audio to video
                //lỗi như trên
                cmd = "-framerate 1/5 -i /storage/emulated/0/PICTURES/a/img%03d.png -i /storage/emulated/0/PICTURES/a/wing.mp3 -c:v libx264 -c:a copy -shortest /storage/emulated/0/PICTURES/a/audiowithimage2.mp4";

                //code chạy được,
                // ghép các hình lại với nhau thành slideshow
                String cmd1 = "-framerate 1/5 -i /storage/emulated/0/PICTURES/a/img%03d.png -c:v libx264 -r 30 -pix_fmt yuv420p /storage/emulated/0/PICTURES/a/slideshow.mp4";

                //code concat chạy được, ngon chim <3
                cmd1 = "-f concat -safe 0 -i /storage/emulated/0/PICTURES/a/input.txt -c:v libx264 -r 30 -pix_fmt yuv420p /storage/emulated/0/PICTURES/a/slideshow.mp4";

                //code chạy được
                //thêm nhạc vào video
                String cmd2 = "-i /storage/emulated/0/PICTURES/a/slideshow.mp4 -i /storage/emulated/0/PICTURES/a/wing.mp3 -c copy -map 0:0 -map 1:0 -shortest /storage/emulated/0/PICTURES/a/slideshow2.mp4";

                String cmd3;
                //code chạy được ,thêm hiệu ứng fade
                //b1: tạo video base, b2: thêm fade in, b3:them fade out, b4: thêm nhạc
                cmd3 = "-f concat -safe 0 -i /storage/emulated/0/PICTURES/a/input.txt -c:v libx264 -r 30 -pix_fmt yuv420p /storage/emulated/0/PICTURES/a/slideshow.mp4";
                cmd3 = "-i /storage/emulated/0/PICTURES/a/slideshow.mp4 -y -vf fade=in:0:60 /storage/emulated/0/PICTURES/a/slide_fade_in.mp4";
                cmd3 = "-i /storage/emulated/0/PICTURES/a/slide_fade_in.mp4 -y -vf fade=out:60:120 /storage/emulated/0/PICTURES/a/slide_fade_out.mp4";
                cmd3 = "-i /storage/emulated/0/PICTURES/a/slide_fade_out.mp4 -i /storage/emulated/0/PICTURES/a/wing.mp3 -c copy -map 0:0 -map 1:0 -shortest /storage/emulated/0/PICTURES/a/slideshow.mp4";

                ///////code chạy được ,thêm hiệu ứng fade
                //b1: tạo video base, b2: thêm fade in, b3:them fade out, b4: thêm nhạc


                //thử nghiệm add effect imaage concat
                //chưa chạy được đang fix
                cmd3 =
                        "-f concat -safe 0 -i /storage/emulated/0/PICTURES/a/input.txt" +
                                " -c:v libx264 -r 30 -pix_fmt yuv420p" +
                                " /storage/emulated/0/PICTURES/a/outeffect.mp4";

                //code chạy được
                cmd3 = "-loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img001.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img002.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img003.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img004.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img005.png" +
                        " -filter_complex" +
                        " [0:v]fade=t=out:st=4:d=1[v0];" +
                        "[1:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v1];" +
                        "[2:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v2];" +
                        "[3:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v3];" +
                        "[4:v]fade=t=in:st=0:d=1,fade=t=out:st=4:d=1[v4];" +
                        "[v0][v1][v2][v3][v4]concat=n=5:v=1:a=0,format=yuv420p[v] -map [v] /storage/emulated/0/PICTURES/a/outconcat.mp4";

                //lỗi
                cmd3 = "-loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img001.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img002.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img003.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img004.png" +
                        " -loop 1 -t 5 -i /storage/emulated/0/PICTURES/a/img005.png" +
                        " -filter_complex" +
                        " [1:v][0:v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b1v];" +
                        "[2:v][1:v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b2v];" +
                        "[3:v][2:v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b3v];" +
                        "[4:v][3:v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b4v];" +
                        "[0:v][b1v][1:v][b2v][2:v][b3v][3:v][b4v][4:v]concat=n=9:v=1:a=0,format=yuv420p[v] -map [v] -threads 16 -preset ultrafast /storage/emulated/0/PICTURES/a/outconcat222.mp4";

                //grey effect
                cmd3 = "-y -i /storage/emulated/0/PICTURES/a/outconcat2.mp4 -strict experimental -vf hue=s=0 -vcodec mpeg4 -b:v 2097152 -r 30 /storage/emulated/0/PICTURES/a/grey.mp4";

                String[] command = cmd1.split(" ");
                String[] commandNext = cmd2.split(" ");
                String[] command3 = cmd3.split(" ");

                //code chạy được
                //hơi lâu tí
                String[] complexCommand = {"-y", "-i", "/storage/emulated/0/PICTURES/a/outconcat2.mp4", "-strict", "experimental", "-filter_complex",
                        "[0:v]colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131[colorchannelmixed];[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0[color_effect]",
                        "-map", "[color_effect]", "-map", "0:a", "-vcodec", "mpeg4", "-b", "15496k", "-ab", "48000", "-ac", "2", "-ar", "22050","-threads","8","-preset","ultrafast", "/storage/emulated/0/PICTURES/a/sephia.mp4"};

                if (command.length != 0) {
                //execFFmpegBinary(command, commandNext);
                    execFFmpegBinary(complexCommand);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}

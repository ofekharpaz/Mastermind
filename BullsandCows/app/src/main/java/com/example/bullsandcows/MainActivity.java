package com.example.bullsandcows;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //public Button[] [] board_game;
    public LinearLayout childLayout[];
    public LinearLayout solution;
    public int selectedColor = -1;
    public Dictionary colorsButtons;
    public ImageButton[] pins_buttons;
    public int[] selected_colors_img = new int[]{R.drawable.red_pin_pressed, R.drawable.yellow_pin_pressed,
            R.drawable.green_pin_pressed, R.drawable.purple_pin_pressed, R.drawable.azure_pin_pressed, R.drawable.purple_pin_pressed};
    int turn = 0;
    public ImageView[][] boolAndPgia;
    public final ArrayList<Integer> decode = new ArrayList<Integer>();
    ;
    public String colors[];
    public int selectedColors[];
    int[] myImageList = new int[]{R.drawable.red_pin, R.drawable.yellow_pin,
            R.drawable.green_pin, R.drawable.purple_pin, R.drawable.azure_pin, R.drawable.pink_pin};
    final int amountOfColors = 6;
    final int amountCorrect = 4;
    final int amountOfGuesses=15;
    private int longClickDuration = 3000;
    private boolean isLongPress = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //==========================================================================================
        //INITIALISE
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout container = (LinearLayout) findViewById(R.id.MainBoard);
        LinearLayout sol_cont = (LinearLayout) findViewById(R.id.Solution);
        Button endTurn = (Button) findViewById(R.id.endTurn);
        Button reveal = (Button) findViewById(R.id.revealCode);
        childLayout = new LinearLayout[amountOfGuesses];
        boolAndPgia = new ImageView[amountOfGuesses][amountCorrect];
        pins_buttons = new ImageButton[amountOfColors];
        selectedColors = new int[amountCorrect];
        for (int i = 0; i < selectedColors.length; i++)
            selectedColors[i] = -1;
        int hole_unfilled = R.drawable.hole_unfilled3;
        solution = new LinearLayout(this);
        solution.setOrientation(LinearLayout.HORIZONTAL);
        //==========================================================================================
        //Create the code for user
        Random randomGenerator = new Random();
        while (decode.size() < amountCorrect) {

            int random = randomGenerator.nextInt(amountOfColors);
            if (!decode.contains(random)) {
                decode.add(random);
            }
        }
        //==========================================================================================
        // Click listener for the reveal button
        reveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), whatColorisNumber(decode.get(0)) + ", " + whatColorisNumber(decode.get(1)) +
                                ", " + whatColorisNumber(decode.get(2)) + ", " + whatColorisNumber(decode.get(3)),
                        Toast.LENGTH_LONG).show();
            }
        });
        //==========================================================================================
        // Click listener for the end turn button
        endTurn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                for (int i = 0; i < 4; i++) {
                    if (selectedColors[i] == -1) {
                        Toast.makeText(getBaseContext(), "You need to fill the entire row first",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                int numPgia = 0, numBools = 0;
                for (int i = 0; i < selectedColors.length; i++) {
                    if (decode.get(i) == selectedColors[i])
                        numBools++;
                    else if (decode.contains(selectedColors[i]))
                        numPgia++;
                }
                // if bools = 4 then announce winning
                if (numBools == amountCorrect) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Announcement");
                    alertDialog.setMessage("You've won!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }


                int i = 0;
                for (; i < numBools; i++)
                    boolAndPgia[turn][i].setImageResource(R.drawable.bool);
                for (int j = 0; j < numPgia; j++, i++)
                    boolAndPgia[turn][i].setImageResource(R.drawable.pgia);
                for (i = 0; i < amountOfColors; i++) {

                    pins_buttons[i].setEnabled(true);
                    pins_buttons[i].setBackgroundResource(myImageList[i]);
                    if (i < amountCorrect) {
                        ImageButton btn = findViewById(i + 1 + (turn * 4)+amountOfColors);
                        btn.setEnabled(false);
                        selectedColors[i] = -1;
                    }
                }
                selectedColor = -1;
                turn++;
                if(turn==amountOfGuesses) { // you just lost
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Announcement");
                    alertDialog.setMessage("You lost :(  </3");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    for(i=0; i<amountCorrect; i++)
                        selectedColors[i]=-1;
                }
            }
        });
        //==========================================================================================
        // Create the player pins for him to drag
        for (int i = 0; i < amountOfColors; i++) {
            LinearLayout lyr = new LinearLayout(this);
            final ImageButton btn = new ImageButton(this);
            btn.setId(i);
            btn.setBackgroundResource(myImageList[i]);
            btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setAdjustViewBounds(true);
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) { // event listener for dragging
                    ClipData data = ClipData.newPlainText("id", String.valueOf(view.getId()));
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(btn);
                    view.startDrag(data, shadow, null, 0);
                    return true;
                }
            });
            //colorsButtons.put(colors[i], btn);
            lyr.addView(btn);
            LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(120, 217);
            par.setMargins(10, 0, 10, 0);
            lyr.setLayoutParams(par);
            pins_buttons[i] = btn;
            solution.addView(lyr);
        }
        solution.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        solution.setGravity(Gravity.CENTER);
        sol_cont.addView(solution);
        //==========================================================================================
        // Create the game board and the bool pgia holes
        for (int i = 0; i < childLayout.length; i++) {

            childLayout[i] = new LinearLayout(this);
            childLayout[i].setId(i);
            childLayout[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            childLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            childLayout[i].setGravity(Gravity.CENTER_HORIZONTAL);
            for (int j = 0; j < amountCorrect; j++) {
                ImageButton btn = new ImageButton(this);
                LinearLayout lyr = new LinearLayout(this);
                //btn.setLayoutParams(params);
                btn.setOnClickListener(this);
                btn.setId(j + 1 + (i * 4)+amountOfColors);
                //btn.setText(String.valueOf(j + 1 + (i * 4)));
                btn.setBackgroundResource(hole_unfilled);
                btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                btn.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View view, DragEvent dragEvent) {
                        final int action = dragEvent.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                break;
                            case DragEvent.ACTION_DRAG_EXITED:
                                break;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                break;

                            case DragEvent.ACTION_DROP: {

                            }

                            case DragEvent.ACTION_DRAG_ENDED: {
                                try {


                                    ClipData.Item item = dragEvent.getClipData().getItemAt(0); // the button that was dragged
                                    int color = Integer.parseInt(item.getText().toString());
                                    int col =(view.getId()-amountOfColors-1)%amountCorrect;
                                    ImageButton dragged = findViewById(color);
                                    if(selectedColors[col]!=-1) { // if it already contained color
                                        int colorAlready=selectedColors[col];
                                        ImageButton restore = findViewById(colorAlready);
                                        restore.setBackgroundResource(myImageList[colorAlready]);
                                        restore.setEnabled(true);
                                    }
                                    dragged.setEnabled(false);
                                    dragged.setBackgroundResource(selected_colors_img[color]);
                                    view.setBackgroundResource(myImageList[color]);
                                    selectedColors[col] = color;
                                }
                                catch (Exception e) {
                                    break;
                                }
                            }
                            default:
                                break;
                        }
                        return  true;
                    }
                });
                btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //btn.setLayoutParams(new ViewGroup.LayoutParams(300, 200));
                btn.setAdjustViewBounds(true);
                //colorsButtons.put(colors[i], btn);
                lyr.addView(btn);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(120, 120);
                par.setMargins(40, 50, 20, 50);
                lyr.setLayoutParams(par);
                childLayout[i].addView(lyr);
            }
            
            LinearLayout bool_pgia = new LinearLayout(this);
            bool_pgia.setOrientation(LinearLayout.VERTICAL);
            int counter = 0;
            for (int k = 0; k < amountCorrect / 2; k++) {
                LinearLayout layer = new LinearLayout(this);
                layer.setOrientation(LinearLayout.HORIZONTAL);
                for (int y = 0; y < amountCorrect / 2; y++) {
                    ImageView img = new ImageView(this);
                    img.setImageResource(R.drawable.hole_unfilled3);
                    img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    img.setMaxWidth(100);
                    img.setMaxHeight(100);
                    img.setAdjustViewBounds(true);
                    layer.addView(img);
                    boolAndPgia[i][counter] = img;
                    counter++;
                    if (counter == amountCorrect)
                        break;
                }
                bool_pgia.addView(layer);
            }
            childLayout[i].addView(bool_pgia);
            container.addView(childLayout[i]);


        }
        //==========================================================================================

    }

    private String whatColorisNumber(int n) {
        if (n == 0)
            return "Red";
        if (n == 1)
            return "Yellow";
        if (n == 2)
            return "Green";
        if (n == 3)
            return "Purple";
        if (n == 4)
            return "Azure";
        else
            return "Pink";

    }

    public void onClick(View V) {
        int id = V.getId();
        int col = (id - amountOfColors-1) % amountCorrect;
        if(selectedColors[col]!=-1)
        {
            int colorAlready=selectedColors[col];
            ImageButton restore = findViewById(colorAlready);
            restore.setBackgroundResource(myImageList[colorAlready]);
            restore.setEnabled(true);
            V.setBackgroundResource(R.drawable.hole_unfilled3);
            selectedColors[col]=-1;
        }
        /*
        if (id < 0) {
            row = -1;
            col = ((id + 1) % amountOfColors) * -1;
        } else {
            row = turn;
        }
        // if we clicked a button from top bar
        if (row < 0) {
            selectedColor = col;
        }
        // else we put it in the selected block
        else {
            if (selectedColor != -1) { // if indeed a color was picked beforehand
                if (selectedColors[col] == -1) { // if a color wasnt placed before
                    selectedColors[col] = selectedColor;
                } else // there is already a color on the button we pressed, we switch between them
                {
                    int indToReactive = selectedColors[col];
                    selectedColors[col] = selectedColor;
                    pins_buttons[indToReactive].setEnabled(true);
                    pins_buttons[indToReactive].setBackgroundResource(myImageList[indToReactive]);
                }
                V.setBackgroundResource(myImageList[selectedColor]);
                pins_buttons[selectedColor].setEnabled(false);
                pins_buttons[selectedColor].setBackgroundResource(selected_colors_img[selectedColor]);
                selectedColor = -1;
            }
            // if a color wasnt picked, and we clicked on a button from the board
            else {
                if (selectedColors[col] != -1)//returns the color if exists to active and cleans the button image
                {
                    int ind = selectedColors[col];
                    pins_buttons[ind].setEnabled(true);
                    pins_buttons[ind].setBackgroundResource(myImageList[ind]);
                    V.setBackgroundResource(R.drawable.hole_unfilled3);
                    selectedColors[col] = -1;
                }
            }


        }

         */
    }
}
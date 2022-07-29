package androidx.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.R;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏闯关
 */
public class GameStageView extends View {

    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 解锁背景颜色
     */
    private int itemUnlockBackgroundColor = Color.parseColor("#3BD2B3");
    /**
     * 未开锁背景颜色
     */
    private int itemLockBackgroundColor = Color.parseColor("#DDDDDD");
    /**
     * 三角颜色
     */
    private int triangleColor = Color.parseColor("#3BD2B3");
    /**
     * 倒三角值
     */
    private float triangleSize = dip(8);
    /**
     * 左边间距
     */
    private float innerPaddingLeft = dip(15);
    /**
     * 上边间距
     */
    private float innerPaddingTop = dip(60);
    /**
     * 右边间距
     */
    private float innerPaddingRight = dip(15);
    /**
     * item高度
     */
    private float itemHeight = dip(28);
    /**
     * item宽度
     */
    private float itemWidth = dip(55);
    /**
     * item头像资源
     */
    private int itemHeadSrc = 0;
    /**
     * item圆角
     */
    private float itemRadius = dip(5);
    /**
     * 文字大小
     */
    private float textSize = dip(12);
    /**
     * 普通文字颜色
     */
    private int textUnlockColor = Color.parseColor("#FFFFFF");
    /**
     * 未解锁文字颜色
     */
    private int textLockColor = Color.parseColor("#FFFFFF");
    /**
     * 分割线颜色
     */
    private float dividerHorizontalWidth = dip(10);
    /**
     * 分割线高度
     */
    private float dividerHorizontalHeight = dip(5);
    /**
     * 分割线垂直宽度
     */
    private float dividerVerticalWidth = dip(5);
    /**
     * 分割线垂直高度
     */
    private float dividerVerticalHeight = dip(60);
    /**
     * 行列数
     */
    private int columnCount = 5;
    /**
     * 总行列数
     */
    private int itemCount = 26;
    /**
     * 现在位置
     */
    private int itemPosition = 13;
    /**
     * 头像圆圈颜色
     */
    private int headCircleColor = Color.parseColor("#FFFFFF");
    /**
     * 头像大小
     */
    private int headSize = 80;
    /**
     * 头像上间距
     */
    private float headArrowMarginTop = dip(2);
    /**
     * 头像边线颜色
     */
    private int headStrokeColor = Color.parseColor("#DDDDDD");
    /**
     * 头像边线宽度
     */
    private int headStrokeWidth = 8;


    /**
     * 正在绘制item个数
     */
    private int drawingItemCount = 1;
    /**
     * item水平移动距离
     */
    private float itemHorizontalMove = 0;
    /**
     * Item垂直移动距离
     */
    private float itemVerticalMove = 0;
    /**
     * 该行是 mod position 等于 0 的项目总数
     */
    private int fullRow;
    /**
     * 行余数
     */
    private int rowRemainder;
    /**
     * 行位置
     */
    private int rowPosition;
    /**
     * 总行数
     */
    private int totalRow;
    /**
     * 行位置
     */
    private int columnPosition = 0;
    /**
     * 画布
     */
    private Canvas canvas;
    /**
     * Item坐标数据
     */
    private Map<String, String> itemCoordinates;
    /**
     * 头像坐标
     */
    private Map<String, String> headCoordinates;
    /**
     * 头像Bitmap
     */
    private Bitmap headBitmap;


    public GameStageView(Context context) {
        super(context);
        initAttributeSet(context, null, 0);
    }

    public GameStageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs, 0);
    }

    public GameStageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs, defStyleAttr);
    }

    public GameStageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributeSet(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                //item 点击事件
                for (Map.Entry<String, String> entry : itemCoordinates.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String coordinateStart[] = value.split("&")[0].split(",");
                    String coordinateEnd[] = value.split("&")[1].split(",");
                    int clickPosition = Integer.parseInt(key);
                    if (Float.parseFloat(coordinateStart[0]) <= x && x <= Float.parseFloat(coordinateEnd[0]) && Float.parseFloat(coordinateStart[1]) <= y && y <= Float.parseFloat(coordinateEnd[1])) {
                        if (onGameItemClickListener != null) {
                            onGameItemClickListener.onGameItemClick(clickPosition, clickPosition <= itemPosition ? true : false);
                        }
                    }
                }
                //头像点击事件
                for (Map.Entry<String, String> entry : headCoordinates.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String coordinateStart[] = value.split("&")[0].split(",");
                    String coordinateEnd[] = value.split("&")[1].split(",");
                    int clickPosition = Integer.parseInt(key);
                    if (Float.parseFloat(coordinateStart[0]) <= x && x <= Float.parseFloat(coordinateEnd[0]) && Float.parseFloat(coordinateStart[1]) <= y && y <= Float.parseFloat(coordinateEnd[1])) {
                        if (onGameHeadClickListener != null) {
                            onGameHeadClickListener.onGameHeadClick(clickPosition, clickPosition <= itemPosition ? true : false);
                        }
                    }
                }
                break;
        }
        return true;
    }

    /**
     * xml属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttributeSet(Context context, AttributeSet attrs, int defStyleAttr) {
        itemCoordinates = new HashMap<>();
        headCoordinates = new HashMap<>();
        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GameStageView, defStyleAttr, 0);
            itemWidth = array.getDimension(R.styleable.GameStageView_itemWidth, itemWidth);
            itemHeight = array.getDimension(R.styleable.GameStageView_itemHeight, itemHeight);
            itemRadius = array.getDimension(R.styleable.GameStageView_itemRadius, itemRadius);
            dividerHorizontalWidth = (int) array.getDimension(R.styleable.GameStageView_itemDividerHorizontalWidth, dividerHorizontalWidth);
            dividerHorizontalHeight = (int) array.getDimension(R.styleable.GameStageView_itemDividerHorizontalHeight, dividerHorizontalHeight);
            dividerVerticalWidth = (int) array.getDimension(R.styleable.GameStageView_itemDividerVerticalWidth, dividerVerticalWidth);
            dividerVerticalHeight = (int) array.getDimension(R.styleable.GameStageView_itemDividerVerticalHeight, dividerVerticalHeight);
            itemCount = array.getInt(R.styleable.GameStageView_itemCount, itemCount);
            columnCount = array.getInt(R.styleable.GameStageView_columnCount, columnCount);
            itemPosition = array.getInt(R.styleable.GameStageView_itemPosition, itemPosition);
            textSize = (int) array.getDimension(R.styleable.GameStageView_itemTextSize, textSize);
            textUnlockColor = array.getColor(R.styleable.GameStageView_itemUnlockTextColor, textUnlockColor);
            textLockColor = array.getColor(R.styleable.GameStageView_itemLockTextColor, textLockColor);
            itemUnlockBackgroundColor = array.getColor(R.styleable.GameStageView_itemUnlockBackgroundColor, itemUnlockBackgroundColor);
            itemLockBackgroundColor = array.getColor(R.styleable.GameStageView_itemLockBackgroundColor, itemLockBackgroundColor);
            innerPaddingLeft = (int) array.getDimension(R.styleable.GameStageView_InnerPaddingLeft, innerPaddingLeft);
            innerPaddingTop = (int) array.getDimension(R.styleable.GameStageView_InnerPaddingTop, innerPaddingTop);
            innerPaddingRight = (int) array.getDimension(R.styleable.GameStageView_InnerPaddingRight, innerPaddingRight);
            headSize = (int) array.getDimension(R.styleable.GameStageView_itemHeadSize, headSize);
            headStrokeColor = array.getColor(R.styleable.GameStageView_itemHeadStrokeColor, headStrokeColor);
            headStrokeWidth = (int) array.getDimension(R.styleable.GameStageView_itemHeadStrokeWidth, headStrokeWidth);
            triangleColor = array.getColor(R.styleable.GameStageView_itemHeadArrowColor, triangleColor);
            headArrowMarginTop = (int) array.getDimension(R.styleable.GameStageView_headArrowMarginTop, headArrowMarginTop);
            itemHeadSrc = array.getResourceId(R.styleable.GameStageView_itemHeadSrc, itemHeadSrc);
            array.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        fullRow = itemCount / columnCount;
        rowRemainder = itemCount % columnCount;
        totalRow = fullRow + (rowRemainder == 0 ? 0 : 1);
        dividerHorizontalWidth = (getMeasuredWidth() - innerPaddingLeft - innerPaddingRight - itemWidth * columnCount) / (columnCount - 1);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int w = widthSpecSize;
        int h = heightSpecSize;
        int needHeight = (int) ((dividerVerticalHeight + itemHeight) * totalRow + innerPaddingTop);
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            w = widthSpecSize;
            h = needHeight;
        }
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        for (int row = 1; row <= totalRow; row++) {
            rowPosition = row;
            itemVerticalMove = (row - 1) * (dividerVerticalHeight + itemHeight);
            int rowColumns = row <= fullRow ? columnCount : rowRemainder;
            //奇数行
            if (row % 2 != 0) {
                for (int column = 1; column <= rowColumns; column++) {
                    columnPosition = column;
                    drawingItemCount = (row - 1) * columnCount + column;
                    itemHorizontalMove = (column - 1) * (itemWidth + dividerHorizontalWidth);
                    String text = drawingItemCount <= itemPosition ? "第" + drawingItemCount + "关" : "未解锁";
                    drawItems(text);
                }
            } else {
                if (rowColumns == columnCount) {
                    rowColumns = 1;
                }
                if (row == totalRow) {
                    rowColumns = rowRemainder > 0 ? columnCount - rowRemainder + 1 : 0;
                }
                for (int column = columnCount; column >= rowColumns; column--) {
                    columnPosition = column;
                    drawingItemCount = (rowPosition * columnCount - column + 1);
                    itemHorizontalMove = (columnCount - (columnCount - column) - 1) * (itemWidth + dividerHorizontalWidth);
                    String text = drawingItemCount <= itemPosition ? "第" + (rowPosition * columnCount - column + 1) + "关" : "未解锁";
                    drawItems(text);
                }
            }
        }
    }

    /**
     * 绘制item
     *
     * @param text 文字
     */
    private void drawItems(String text) {
        drawRoundRect(canvas);
        drawRoundRectText(canvas, text);
        drawHorizontalDivider(canvas);
        drawVerticalDivider(canvas);
        drawHeads(canvas);
    }

    /**
     * 圆角item
     *
     * @param canvas
     */
    private void drawRoundRect(Canvas canvas) {
        paint = new Paint();
        paint.setColor(drawingItemCount <= itemPosition ? itemUnlockBackgroundColor : itemLockBackgroundColor);
        RectF rectF = new RectF();
        rectF.left = innerPaddingLeft + itemHorizontalMove;
        rectF.right = rectF.left + itemWidth;
        rectF.top = innerPaddingTop + itemVerticalMove;
        rectF.bottom = rectF.top + itemHeight;
        canvas.drawRoundRect(rectF, itemRadius, itemRadius, paint);
        //item坐标记录
        itemCoordinates.put(drawingItemCount + "", rectF.left + "," + rectF.top + "&" + rectF.right + "," + rectF.bottom);
    }

    /**
     * 圆角文字
     *
     * @param canvas
     * @param text
     */
    private void drawRoundRectText(Canvas canvas, String text) {
        paint = new Paint();
        paint.setColor(drawingItemCount <= itemPosition ? textUnlockColor : textLockColor);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textWidth = bounds.width();
        int textHeight = bounds.height();
        canvas.drawText(text, innerPaddingLeft + itemHorizontalMove + itemWidth / 2F - textWidth / 2F, innerPaddingTop + itemVerticalMove + (itemHeight / 2 + textHeight / 2F), paint);
    }

    /**
     * 水平分割线
     *
     * @param canvas
     */
    private void drawHorizontalDivider(Canvas canvas) {
        //每行最后
        if (columnPosition % columnCount == 0) {
            return;
        }
        //奇数行最后一个item
        if (rowPosition % 2 != 0 && rowPosition == totalRow && columnPosition == rowRemainder) {
            return;
        }
        paint = new Paint();
        paint.setColor(drawingItemCount <= itemPosition ? itemUnlockBackgroundColor : itemLockBackgroundColor);
        Rect rect = new Rect();
        rect.left = (int) (innerPaddingLeft + itemWidth + itemHorizontalMove);
        rect.right = (int) (rect.left + dividerHorizontalWidth);
        rect.top = (int) (innerPaddingTop + itemHeight / 2 - dividerHorizontalHeight / 2 + itemVerticalMove);
        rect.bottom = (int) (rect.top + dividerHorizontalHeight);
        canvas.drawRect(rect, paint);
    }

    /**
     * 垂直分割线
     *
     * @param canvas
     */
    private void drawVerticalDivider(Canvas canvas) {
        //基数行最后一个Item显示,偶数行第一个Item显示,数据的最后一个不显示
        if ((rowPosition % 2 == 0 && columnPosition == 1) || (rowPosition % 2 != 0 && columnPosition == columnCount)) {
            if (drawingItemCount == itemCount) {
                return;
            }
            paint = new Paint();
            paint.setColor(drawingItemCount <= itemPosition ? itemUnlockBackgroundColor : itemLockBackgroundColor);
            Rect rect = new Rect();
            rect.left = (int) (innerPaddingLeft + itemWidth / 2 - dividerHorizontalHeight / 2 + itemHorizontalMove);
            rect.right = (int) (rect.left + dividerVerticalWidth);
            rect.top = (int) (innerPaddingTop + itemHeight + itemVerticalMove);
            rect.bottom = (int) (rect.top + dividerVerticalHeight);
            canvas.drawRect(rect, paint);
        }
    }

    /**
     * 绘制头像
     *
     * @param canvas
     */
    private void drawHeads(Canvas canvas) {
        if (drawingItemCount != itemPosition) {
            return;
        }
        float x = innerPaddingLeft + itemWidth / 2F - headSize / 2F + itemHorizontalMove;
        float y = innerPaddingTop - headSize - triangleSize - headArrowMarginTop + itemVerticalMove;
        //记录头像坐标范围
        headCoordinates.put(drawingItemCount + "", (x - headSize / 2F) + "," + (y - headSize / 2) + "&" + (x + headSize) + "," + (y + headSize));
        paint = new Paint();
        //圆形头像
        Bitmap output = drawCircleHead();
        //画生成的图片
        canvas.drawBitmap(output, x, y, paint);
        //三角形
        drawTriangle(canvas, x, y);
        //边框
        drawCircleStroke(canvas, x, y);
    }

    /**
     * 绘制圆圈头像
     *
     * @return 圆圈头像bitmap
     */
    private Bitmap drawCircleHead() {
        paint = new Paint();
        Bitmap output = Bitmap.createBitmap(headSize, headSize, Bitmap.Config.ARGB_8888);
        Canvas headCanvas = new Canvas(output);
        paint.setAntiAlias(true);
        headCanvas.drawARGB(0, 0, 0, 0);
        paint.setColor(headCircleColor);
        //圆圈
        headCanvas.drawCircle(headSize / 2F, headSize / 2F, headSize / 2F, paint);
        //设置重叠模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //矩形范围
        Rect rect = new Rect(0, 0, headSize, headSize);
        Bitmap bitmap = headBitmap != null ? headBitmap : BitmapFactory.decodeResource(getResources(), itemHeadSrc);
        if (bitmap != null) {
            headCanvas.drawBitmap(bitmap, rect, rect, paint);
            //对大图进行缩放
            Matrix matrix = new Matrix();
            float sx = headSize / bitmapWidthHeight(itemHeadSrc)[0];
            float sy = headSize / bitmapWidthHeight(itemHeadSrc)[1];
            matrix.setScale(sx, sy, 0, 0);
            headCanvas.drawBitmap(bitmap, matrix, paint);
        }
        //重置画笔
        paint.reset();
        return output;
    }

    /**
     * 绘制三角形
     *
     * @param canvas
     * @param x
     * @param y
     */
    private void drawTriangle(Canvas canvas, float x, float y) {
        Paint paint = new Paint();
        paint.setColor(triangleColor);
        paint.setAntiAlias(true);
        Path path = new Path();
        path.moveTo(headSize / 2 - triangleSize / 2f + x, headSize + headArrowMarginTop + y);
        path.lineTo(headSize / 2 + triangleSize / 2f + x, headSize + headArrowMarginTop + y);
        path.lineTo(headSize / 2 + x, (float) (headSize + triangleSize * Math.tan(Math.toRadians(60)) / 2F + headArrowMarginTop + y));
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制头像边线
     *
     * @param canvas
     * @param x
     * @param y
     */
    private void drawCircleStroke(Canvas canvas, float x, float y) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(headStrokeWidth);
        paint.setColor(headStrokeColor);
        canvas.drawCircle(headSize / 2 + x, headSize / 2 + y, headSize / 2, paint);
        paint.reset();
    }

    /**
     * @param resId 图资源
     * @return 图片宽搞
     */
    private float[] bitmapWidthHeight(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        float width = options.outWidth;
        float height = options.outHeight;
        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;
        return new float[]{width, height};
    }

    /**
     * 尺寸转换
     *
     * @param dp dip值
     * @return px值
     */
    private float dip(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public OnGameHeadClickListener onGameHeadClickListener;

    /**
     * 设置头像点击事件
     *
     * @param onGameHeadClickListener
     */
    public void setOnGameHeadClickListener(OnGameHeadClickListener onGameHeadClickListener) {
        this.onGameHeadClickListener = onGameHeadClickListener;
    }

    /**
     * 头像点击事件
     */
    public interface OnGameHeadClickListener {

        /**
         * item头像点击事件
         *
         * @param position 位置
         * @param lock     是否未解锁
         */
        void onGameHeadClick(int position, boolean lock);
    }

    public OnGameItemClickListener onGameItemClickListener;

    /**
     * 设置item点击事件
     *
     * @param onGameItemClickListener
     */
    public void setOnGameItemClickListener(OnGameItemClickListener onGameItemClickListener) {
        this.onGameItemClickListener = onGameItemClickListener;
    }

    /**
     * item点击事件
     */
    public interface OnGameItemClickListener {

        /**
         * item点击事件
         *
         * @param position 位置
         * @param lock     是否未解锁
         */
        void onGameItemClick(int position, boolean lock);
    }

    /**
     * @return 头像资源Bitmap
     */
    public Bitmap getHeadBitmap() {
        return headBitmap;
    }

    /**
     * @return 解锁背景颜色
     */
    public int getItemUnlockBackgroundColor() {
        return itemUnlockBackgroundColor;
    }

    /**
     * 设置解锁背景颜色
     *
     * @param itemUnlockBackgroundColor
     */
    public void setItemUnlockBackgroundColor(int itemUnlockBackgroundColor) {
        this.itemUnlockBackgroundColor = itemUnlockBackgroundColor;
        invalidate();
    }

    /**
     * @return 未解锁背景颜色
     */
    public int getItemLockBackgroundColor() {
        return itemLockBackgroundColor;
    }

    /**
     * 设置未解锁背景颜色
     *
     * @param itemLockBackgroundColor
     */
    public void setItemLockBackgroundColor(int itemLockBackgroundColor) {
        this.itemLockBackgroundColor = itemLockBackgroundColor;
        invalidate();
    }

    /**
     * @return 三角形颜色
     */
    public int getTriangleColor() {
        return triangleColor;
    }

    /**
     * 设置三角形颜色
     *
     * @param triangleColor
     */
    public void setTriangleColor(int triangleColor) {
        this.triangleColor = triangleColor;
        invalidate();
    }

    /**
     * @return 三角形边长
     */
    public float getTriangleSize() {
        return triangleSize;
    }

    /**
     * 设置三角形边长
     *
     * @param triangleSize
     */
    public void setTriangleSize(float triangleSize) {
        this.triangleSize = triangleSize;
        invalidate();
    }

    /**
     * @return 内部左边间距
     */
    public float getInnerPaddingLeft() {
        return innerPaddingLeft;
    }

    /**
     * 设置内部左边间距
     *
     * @param innerPaddingLeft
     */
    public void setInnerPaddingLeft(float innerPaddingLeft) {
        this.innerPaddingLeft = innerPaddingLeft;
        invalidate();
    }

    /**
     * @return 内部上间距
     */
    public float getInnerPaddingTop() {
        return innerPaddingTop;
    }

    /**
     * 设置内部上间距
     *
     * @param innerPaddingTop
     */
    public void setInnerPaddingTop(float innerPaddingTop) {
        this.innerPaddingTop = innerPaddingTop;
        invalidate();
    }

    /**
     * @return 内部右间距
     */
    public float getInnerPaddingRight() {
        return innerPaddingRight;
    }

    /**
     * 设置内部右间距
     *
     * @param innerPaddingRight
     */
    public void setInnerPaddingRight(float innerPaddingRight) {
        this.innerPaddingRight = innerPaddingRight;
        invalidate();
    }

    /**
     * @return item高度
     */
    public float getItemHeight() {
        return itemHeight;
    }

    /**
     * 设置item高度
     *
     * @param itemHeight
     */
    public void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
        invalidate();
    }

    /**
     * @return item宽度
     */
    public float getItemWidth() {
        return itemWidth;
    }

    /**
     * 设置item宽度
     *
     * @param itemWidth
     */
    public void setItemWidth(float itemWidth) {
        this.itemWidth = itemWidth;
        invalidate();
    }

    /**
     * @return 头像资源
     */
    public int getItemHeadSrc() {
        return itemHeadSrc;
    }

    /**
     * 设置头像资源
     *
     * @param itemHeadSrc
     */
    public void setItemHeadSrc(int itemHeadSrc) {
        this.itemHeadSrc = itemHeadSrc;
        invalidate();
    }

    /**
     * @return item圆角大小
     */
    public float getItemRadius() {
        return itemRadius;
    }

    /**
     * 设置item圆角大小
     *
     * @param itemRadius
     */
    public void setItemRadius(float itemRadius) {
        this.itemRadius = itemRadius;
        invalidate();
    }

    /**
     * @return 文字大小
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    /**
     * @return 文字解锁颜色
     */
    public int getTextUnlockColor() {
        return textUnlockColor;
    }

    /**
     * 设置文字解锁颜色
     *
     * @param textUnlockColor
     */
    public void setTextUnlockColor(int textUnlockColor) {
        this.textUnlockColor = textUnlockColor;
        invalidate();
    }

    /**
     * @return 文字未解锁颜色
     */
    public int getTextLockColor() {
        return textLockColor;
    }

    /**
     * 设置文字未解锁颜色
     *
     * @param textLockColor
     */
    public void setTextLockColor(int textLockColor) {
        this.textLockColor = textLockColor;
        invalidate();
    }

    /**
     * @return 水平分割线宽度
     */
    public float getDividerHorizontalWidth() {
        return dividerHorizontalWidth;
    }

    /**
     * 设置水平分割线宽度
     *
     * @param dividerHorizontalWidth
     */
    public void setDividerHorizontalWidth(float dividerHorizontalWidth) {
        this.dividerHorizontalWidth = dividerHorizontalWidth;
        invalidate();
    }

    /**
     * @return 水平分割线高度
     */
    public float getDividerHorizontalHeight() {
        return dividerHorizontalHeight;
    }

    /**
     * 设置水平分割线高度
     *
     * @param dividerHorizontalHeight
     */
    public void setDividerHorizontalHeight(float dividerHorizontalHeight) {
        this.dividerHorizontalHeight = dividerHorizontalHeight;
        invalidate();
    }

    /**
     * @return 垂直分割线宽度
     */
    public float getDividerVerticalWidth() {
        return dividerVerticalWidth;
    }

    /**
     * 设置垂直分割线宽度
     *
     * @param dividerVerticalWidth
     */
    public void setDividerVerticalWidth(float dividerVerticalWidth) {
        this.dividerVerticalWidth = dividerVerticalWidth;
        invalidate();
    }

    /**
     * @return 垂直分割线高度
     */
    public float getDividerVerticalHeight() {
        return dividerVerticalHeight;
    }

    /**
     * 设置垂直分割线高度
     *
     * @param dividerVerticalHeight
     */
    public void setDividerVerticalHeight(float dividerVerticalHeight) {
        this.dividerVerticalHeight = dividerVerticalHeight;
        invalidate();
    }

    /**
     * @return 总列数
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * 设置总列数
     *
     * @param columnCount
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        invalidate();
    }

    /**
     * @return item总数
     */
    public int getItemCount() {
        return itemCount;
    }

    /**
     * 设置item总数
     *
     * @param itemCount
     */
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        invalidate();
    }

    /**
     * @return item位置
     */
    public int getItemPosition() {
        return itemPosition;
    }

    /**
     * 设置item位置
     *
     * @param itemPosition
     */
    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
        invalidate();
    }

    /**
     * @return 头像圆圈颜色
     */
    public int getHeadCircleColor() {
        return headCircleColor;
    }

    /**
     * 设置头像圆圈颜色
     *
     * @param headCircleColor
     */
    public void setHeadCircleColor(int headCircleColor) {
        this.headCircleColor = headCircleColor;
        invalidate();
    }

    /**
     * @return 头像大小
     */
    public int getHeadSize() {
        return headSize;
    }

    /**
     * 设置头像大小
     *
     * @param headSize
     */
    public void setHeadSize(int headSize) {
        this.headSize = headSize;
        invalidate();
    }

    /**
     * @return 头像箭头上间距
     */
    public float getHeadArrowMarginTop() {
        return headArrowMarginTop;
    }

    /**
     * 设置头像箭头上间距
     *
     * @param headArrowMarginTop
     */
    public void setHeadArrowMarginTop(float headArrowMarginTop) {
        this.headArrowMarginTop = headArrowMarginTop;
        invalidate();
    }

    /**
     * @return 头像线圈颜色
     */
    public int getHeadStrokeColor() {
        return headStrokeColor;
    }

    /**
     * 设置头像线圈颜色
     *
     * @param headStrokeColor
     */
    public void setHeadStrokeColor(int headStrokeColor) {
        this.headStrokeColor = headStrokeColor;
        invalidate();
    }

    /**
     * @return 头像线圈宽度
     */
    public int getHeadStrokeWidth() {
        return headStrokeWidth;
    }

    /**
     * 设置头像线圈宽度
     *
     * @param headStrokeWidth
     */
    public void setHeadStrokeWidth(int headStrokeWidth) {
        this.headStrokeWidth = headStrokeWidth;
        invalidate();
    }

}
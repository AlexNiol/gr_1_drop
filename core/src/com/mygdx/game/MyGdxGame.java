package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {
    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;
    private OrthographicCamera camera; // для рендера изображения размером 800x480
    private SpriteBatch batch; // для отрисовки 2d-изображений, в нашем случае для Texture
    private Rectangle bucket;
    private Array<Rectangle> rainDrops;
    private long lastDropItem;
    private Texture dropImage2;
    private Array<Rectangle> bombDrops;


    private void spawnRainDrop() {
        Rectangle rainDrop = new Rectangle();
        rainDrop.x = MathUtils.random(0, 800 - 64);
        rainDrop.y = 480;
        rainDrop.width = 64;
        rainDrop.height = 64;
        rainDrops.add(rainDrop);
        lastDropItem = TimeUtils.nanoTime();
    }

    private void spawnBombDrop() {
        Rectangle bombDrop = new Rectangle();
        bombDrop.x = MathUtils.random(0, 800 - 64);
        bombDrop.y = 480;
        bombDrop.width = 64;
        bombDrop.height = 64;
        bombDrops.add(bombDrop);
        lastDropItem = TimeUtils.nanoTime();
    }


    @Override
    public void create() {
        super.create();

        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        dropImage2 = new Texture(Gdx.files.internal("aaaa)"));

        rainMusic.setLooping(true);
        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        rainDrops = new Array<Rectangle>();
        spawnRainDrop();
        bombDrops = new Array<Rectangle>();
        spawnRainDrop();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(2, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // обновление камеры
        camera.update();

        // указываем SpriteBatch координаты системы для камеры
        batch.setProjectionMatrix(camera.combined);

        // отрисовка ведра
        batch.begin();
        batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : rainDrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        batch.end();

        // передвижение корзины по экрану
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos); // чтобы клик по экрану
            // расчитывался в пределах viewport'a (ширины и высоты экрана)
            bucket.x = touchPos.x - 64 / 2;
        }

        // перемещение на стрелки клавиатуры
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += 200 * Gdx.graphics.getDeltaTime();
        }

        // делаем, чтобы ведро не уходило за пределы экрана
        if (bucket.x < 0) {
            bucket.x = 0;
        }
        if (bucket.x > 800 - 64) {
            bucket.x = 800 - 64;
        }

        // проверяем сколько времени прошло после последней капельки, если больше 1000...,
        // то создаем новую
        if (TimeUtils.nanoTime() - lastDropItem > 1000000000) {
            spawnRainDrop();
        }

        // падение капель, удаление капель, воспроизведение звука при падении в ведро
        for (Iterator<Rectangle> iter = rainDrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop_loop = iter.next();
            raindrop_loop.y -= 200 * Gdx.graphics.getDeltaTime();

            // как только капля попадает за нижнюю границу, она удаляется
            if (raindrop_loop.y + 64 < 0) {
                iter.remove();
            }

            // если капля пересекат ведро, то выполняется тело условия
            if (raindrop_loop.overlaps(bucket)) {
                dropSound.play();
                iter.remove();
            }
        }
    }


    @Override
    public void dispose() {
        // он удаляет ресурсы созданные Libgdx
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        batch.dispose();
        dropImage2.dispose();
        


        if (TimeUtils.nanoTime() - lastDropItem > 400000000) {
        }
        spawnBombDrop();

        for (Iterator<Rectangle> iter = rainDrops.iterator(); iter.hasNext(); ) {
            Rectangle bombdrop_loop = iter.next();
            bombdrop_loop.y -= 200 * Gdx.graphics.getDeltaTime();

            if (bombdrop_loop.y + 64 < 0) {
                iter.remove();

                for (Rectangle bombdrop : bombDrops) {
                    batch.draw(dropImage2, bombdrop.x, bombdrop.y);

                    if (bombdrop_loop.overlaps(bucket)) {

                        iter.remove();
                    }

                }


            }


        }
    }
}
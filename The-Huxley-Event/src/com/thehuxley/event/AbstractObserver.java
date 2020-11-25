package com.thehuxley.event;


import com.thehuxley.event.data.Configurator;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractObserver extends Thread {

    private ConcurrentLinkedQueue<HashMap<Integer, Object>> inBox = new ConcurrentLinkedQueue<HashMap<Integer, Object>>();
    private Socket socket = null;
    private ObjectInputStream input = null;
    private ObjectOutputStream output;
    private boolean isConnected = false;
    private int count;
    private int sleepTimeFactor;
    private long sleepTime;
    private static final Logger logger = LoggerFactory.getLogger(AbstractObserver.class);

    public AbstractObserver() {
    	logger.debug("Iniciando " + this + "...");
        sleepTime = Long.parseLong(Configurator.getProperties().getProperty("thread.sleep"));
        sleepTimeFactor = 1;
        handShake();
        start();
        startNotifier();
    }

    private void handShake() {
        
        try {
            socket = new Socket(getServerHost(), getServerPort());
            logger.debug("Conectado! Eitaaa pooorra!!!");
            HashMap<Integer, Object> map = new HashMap<Integer, Object>();
            // Registra quais são os eventos em que se tem interesse
            map.put(Event.KEY_EVENT_MASK, getEventMask());
            // Registra o nome do observador
            map.put(Event.KEY_NAME, getObserverName());

            logger.debug("Handshaking...");	
            // Envia a mensagem para o servidor.
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(map);

            // Espera a resposta
            input = new ObjectInputStream(socket.getInputStream());
            HashMap<Integer, Object> fromServer = (HashMap<Integer, Object>) input.readObject();
            Object ackMsg = fromServer.get(Event.KEY_ACK);
            if (ackMsg == null) {
            	logger.error("Não foi possível registrar o observador no servidor. Protocolo errado, a mensagem ACK não foi recebida.");
            }
            isConnected = true;
            sleepTimeFactor = 1;
            
        } catch (Exception e) {
        	logger.error("Erro ao se conectar ao servidor. Motivo: "
                    + e.getMessage());
        }
        if (!isConnected) {
            closeAllStreams();
        }

    }

    private void closeAllStreams() {
        isConnected = false;
        if (output != null) {
            try {
                output.close();
                output = null;
            } catch (Exception e) {
            }
        }
        if (input != null) {
            try {
                input.close();
                input = null;
            } catch (Exception e) {
            }
        }

        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (Exception e) {
            }
        }
    }

    public void startNotifier() {
    	logger.debug("Iniciando a thread de notificação...");
        Thread notifier = new Thread() {
            public void run() {
                while (true) {
                    while (isConnected) {
                        try {
                            if (!inBox.isEmpty()) {
                                logger.debug("Notificando...");
                                performWork();
                            }
                            logger.debug("Dormindo um pouco.");
                            Thread.sleep(loopInterval());
                        } catch (Exception e) {
                        	logger.error("Exceção ocorrida: " + e.getMessage());
                            closeAllStreams();
                        }
                    }
                    try {
                        Thread.sleep(loopInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.error("Não consegui dormir! :(", e);
                    }
                }
            }

        };
        notifier.start();
    }

    public void run() {
        while (true) {
            while (isConnected) {
                HashMap<Integer, Object> fromServer;
                try {
                    fromServer = (HashMap<Integer, Object>) input.readObject();
                    try {
                        if (!filter(inBox, fromServer)) {
                            inBox.add(fromServer);
                        }
                    } catch (Exception e) {
                    	logger.error("Ignorando mensagem do observer. Motivo: ", e);
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                	logger.error("Erro ao receber a mensagem do servidor. ", e);
                    closeAllStreams();
                }
            }
            System.out.println("Observer [" + getName() + ", " + getObserverName()
                    + "] Tentando Reconectar.");
            if (!isConnected) {
                try {
                	logger.debug("Fall asleep: " + sleepTimeFactor * sleepTime);
                    Thread.sleep(sleepTimeFactor * sleepTime);
                    if (sleepTimeFactor <= 10) {
                        sleepTimeFactor++;
                    }
                } catch (InterruptedException e) {
                	logger.debug("Tô com sono!! não consegui dormir! :( " + e.getMessage());
                }

                handShake();

            }
        }
    }

    /**
     * Host onde esse observador irá se conectar
     *
     * @return
     */
    protected abstract String getServerHost();

    /**
     * Porta do host que irá notificar esse observador
     *
     * @return
     */
    protected abstract int getServerPort();

    /**
     * Nome do observador
     *
     * @return
     */
    protected abstract String getObserverName();

    /**
     * Máscara de eventos em que esse observador está interessado.
     *
     * @return
     */
    protected abstract int getEventMask();

    /**
     * Intervalo de tempo em milisegundos entre duas chamadas consecutivas ao
     * método performWork()
     *
     * @return
     */
    protected abstract long loopInterval();

    /**
     * Esse método deve ser sobrescrito para executar as ações quando o observer
     * for notificado.
     */
    protected abstract void performWork();

    /**
     * Retorna true se o novo elemento não deve ser inserido na fila.
     *
     * @param inBox
     * @param newElement
     * @return
     */
    protected boolean filter(
            ConcurrentLinkedQueue<HashMap<Integer, Object>> inBox,
            HashMap<Integer, Object> newElement) {
        return false;
    }

    protected HashMap<Integer, Object> poll() {
        return inBox.poll();
    }

    protected boolean hasMoreElements() {
        return !inBox.isEmpty();
    }

    protected int inBoxSize() {
        return inBox.size();
    }

    protected void add(HashMap<Integer, Object> msg) {
        inBox.add(msg);
    }

}
package Core;

import Enums.Priority;
import Enums.Status;
import lib.exceptions.ElementNotFoundException;
import lib.exceptions.EmptyCollectionException;
import lib.lists.LinkedUnorderedList;

import java.util.ArrayList;
import java.util.List;

public class Task implements Comparable<Task> {

	private final String name; // Nome da tarefa
	private final Priority priority; // Prioridade da tarefa
	private long duration; // Duração da tarefa em ms
	private int memorySize; // Tamanho da memória necessária
	private LinkedUnorderedList<String> deviceList; // Lista de dispositivos associados
	private Status status; // Status atual da tarefa

	/**
	 * Construtor da tarefa com valores padrão.
	 *
	 * @param name     Nome da tarefa.
	 * @param priority Prioridade da tarefa.
	 */
	public Task(String name, Priority priority) {
		this.name = name;
		this.priority = priority;
		this.duration = 1000;
		this.memorySize = 0;
		this.deviceList = new LinkedUnorderedList<>();
		this.status = Status.WAITING;
	}

	// Getters
	public String getName() {
		return this.name;
	}

	public int getMemorySize() {
		return this.memorySize;
	}

	public long getDuration() {
		return this.duration;
	}

	public Status getStatus() {
		return this.status;
	}

	public Priority getPriority() {
		return this.priority;
	}

	/**
	 * Retorna uma cópia da lista de dispositivos necessários.
	 */
	public List<String> getDevices() {
		List<String> devices = new ArrayList<>();
		for (String device : this.deviceList) {
			devices.add(device);
		}
		return devices;
	}

	// Setters
	public void setMemorySize(int memorySize) {
		if (memorySize < 0) {
			throw new IllegalArgumentException("Memory size cannot be negative.");
		}
		this.memorySize = memorySize;
	}

	public void setDuration(long duration) {
		if (duration <= 0) {
			throw new IllegalArgumentException("Duration must be greater than zero.");
		}
		this.duration = duration;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Adiciona um dispositivo necessário à tarefa.
	 *
	 * @param deviceName Nome do dispositivo.
	 */
	public void addDevice(String deviceName) {
		try {
			if (!this.deviceList.contains(deviceName)) {
				this.deviceList.addToRear(deviceName);
				System.out.println("DEVICE " + deviceName + " ADDED TO TASK " + getName());
			} else {
				System.out.println("DEVICE " + deviceName + " ALREADY EXISTS IN TASK " + getName());
			}
		} catch (EmptyCollectionException e) {
			System.err.println("FAILED TO ADD DEVICE: " + deviceName);
		}
	}

	/**
	 * Remove um dispositivo necessário da tarefa.
	 *
	 * @param deviceName Nome do dispositivo.
	 */
	public void removeRequiredDevice(String deviceName) {
		try {
			this.deviceList.remove(deviceName);
			System.out.println("DEVICE " + deviceName + " REMOVED FROM TASK " + getName());
		} catch (EmptyCollectionException | ElementNotFoundException e) {
			System.err.println("FAILED TO REMOVE DEVICE: " + deviceName + ". NOT FOUND.");
		}
	}

	/**
	 * Compara as tarefas com base na prioridade.
	 *
	 * @param other Outra tarefa a ser comparada.
	 * @return Valor da comparação.
	 */
	@Override
	public int compareTo(Task other) {
		return Integer.compare(this.priority.ordinal(), other.getPriority().ordinal());
	}

	/**
	 * Representação em String para debug e visualização.
	 */
	@Override
	public String toString() {
		return String.format("Task{name='%s', priority=%s, duration=%dms, memorySize=%dMB, status=%s, devices=%s}",
				name, priority, duration, memorySize, status, getDevices());
	}
}

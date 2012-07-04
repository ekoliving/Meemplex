package org.openmaji.implementation.server.space.meemstore.orientdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.serialization.OSerializableStream;

@SuppressWarnings("serial")
public class SerializableObject <T> implements OSerializableStream {
	private T object;
	
	public SerializableObject() {
	}
	
	public SerializableObject(T object) {
		this.object = object;
	}
	
	public T getObject() {
		return this.object;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public SerializableObject<T> fromStream(byte[] bytes) throws OSerializationException {
		try {
			ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytes));
			this.object = (T) is.readObject();
			is.close();
		}
		catch (IOException e) {
			throw new OSerializationException("Could not get input stream", e);
		}
		catch (ClassNotFoundException e) {
			throw new OSerializationException("Could not locate class", e);
		}
		return this;
	}

	@Override
	public byte[] toStream() throws OSerializationException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this.object);
			oos.flush();
			oos.close();
		}
		catch (IOException e) {
			throw new OSerializationException("Could not create output stream", e);
		}
		return os.toByteArray();
	}
}
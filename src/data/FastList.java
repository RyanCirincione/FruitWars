package data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A FastList is a name for a custom ArrayList I wrote <br/>
 * 
 * In retrospect, I should have just overrode the 'remove' method <br/>
 * 
 * To remove elements, the last element is swapped into its place, making the
 * operation O(1)
 *
 * @param <T>
 *            The type stored in the list
 */
public class FastList<T> implements List<T>
{

	private Object[] data;
	private int size;

	public FastList()
	{
		this(16);
	}

	public FastList(int initialCapacity)
	{
		data = new Object[initialCapacity];
		size = 0;
	}

	public FastList(T[] initial)
	{
		data = initial;
		size = initial.length;
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public boolean isEmpty()
	{
		return size == 0;
	}

	@Override
	public boolean contains(Object o)
	{
		for (int i = 0; i < size; i++)
			if (data[i].equals(o))
				return true;
		return false;
	}

	@Override
	public Iterator<T> iterator()
	{
		return new FastIterator(this);
	}

	@Override
	public Object[] toArray()
	{
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(E[] a)
	{
		if (a.length < size)
		{
			throw new IllegalArgumentException("Input length to toArray must be at least FastList::size() long.");
		}
		for (int i = 0; i < size; i++)
		{
			a[i] = (E) data[i];
		}
		return a;
	}

	@Override
	public boolean add(T e)
	{
		if (size * 2 < data.length)
		{
			data[size] = e;
			size++;
		} else
		{
			Object[] old = data;
			data = new Object[data.length * 2];
			System.arraycopy(old, 0, data, 0, size);
			add(e);
		}
		return true;
	}

	@Override
	public boolean remove(Object o)
	{
		for (int i = 0; i < size; i++)
		{
			if (data[i].equals(o))
			{
				remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		int oldsize = size;
		for (T obj : c)
			add(obj);
		return oldsize != size;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		throw new NotImplementedException();
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		int oldsize = size;
		for (Object obj : c)
			remove(obj);
		return oldsize != size;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		int oldsize = size;
		for (int i = 0; i < size; i++)
		{
			if (!c.contains(data[i]))
			{
				remove(i);
				i -= 1;
			}
		}
		return oldsize != size;
	}

	@Override
	public void clear()
	{
		size = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index)
	{
		return (T) data[index];
	}

	@Override
	public T set(int index, T element)
	{
		@SuppressWarnings("unchecked")
		T old = (T) data[index];
		data[index] = element;
		return old;
	}

	@Override
	public void add(int index, T element)
	{
		throw new NotImplementedException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T remove(int index)
	{
		if (index > size)
			throw new IndexOutOfBoundsException();
		T value = get(index);
		set(index, (T) data[size - 1]);
		size -= 1;
		return value;
	}

	@Override
	public int indexOf(Object o)
	{
		for (int i = 0; i < size; i++)
			if (data[i].equals(o))
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(Object o)
	{
		for (int i = size - 1; i >= 0; i--)
			if (data[i].equals(o))
				return i;
		return -1;
	}

	@Override
	public ListIterator<T> listIterator()
	{
		throw new NotImplementedException();
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		throw new NotImplementedException();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		throw new NotImplementedException();
	}

	private class FastIterator implements Iterator<T>
	{
		private FastList<T> backed;
		int index = 0;

		public FastIterator(FastList<T> backer)
		{
			backed = backer;
		}

		@Override
		public void remove()
		{
			throw new NotImplementedException();
		}

		@Override
		public boolean hasNext()
		{
			return index < backed.size();
		}

		@Override
		public T next()
		{
			T val = backed.get(index);
			index += 1;
			return val;
		}

	}

	public String toString()
	{
		String str = "[";
		for(int i = 0; i < size; i++)
		{
			str += data[i].toString();
			if(i != size - 1)
				str += ",";
		}
		return str + "]";
	}

}

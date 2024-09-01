package ru.neoflex.neostudy.deal.mapper;

/**
 * Интерфейс, регламентирующий контракт для мапперов.
 * @param <E> тип entity.
 * @param <D> тип DTO.
 */
public interface Mapper<E, D> {
	E dtoToEntity(D d);
}

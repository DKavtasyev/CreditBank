package ru.neoflex.neostudy.deal.mapper;

public interface Mapper<E, D>
{
	E dtoToEntity(D d);
}

package ru.neostudy.neoflex.deal.mapper;

public interface Mapper<E, D>
{
	E dtoToEntity(D d);
}

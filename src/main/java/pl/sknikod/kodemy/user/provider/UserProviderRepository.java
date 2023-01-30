package pl.sknikod.kodemy.user.provider;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserProviderRepository extends PagingAndSortingRepository<UserProvider, Long> {
}
package com.donkey.book.springboot.service.posts;

import com.donkey.book.springboot.domain.posts.Posts;
import com.donkey.book.springboot.domain.posts.PostsRepository;
import com.donkey.book.springboot.web.dto.PostsResponseDto;
import com.donkey.book.springboot.web.dto.PostsSaveRequestDto;
import com.donkey.book.springboot.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id="+id));

        // Dirty Checking - persistence - [SpringDataJpa의 기본옵션] Persistence Context 가 유지됨
        // ==> entity를 수정하면 transaction이 종료되는 시점에 테이블에 반영됨
        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id="+id));

        return new PostsResponseDto(entity);
    }
}
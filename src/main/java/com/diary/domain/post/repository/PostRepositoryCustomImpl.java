package com.diary.domain.post.repository;

import com.diary.domain.member.model.Member;
import com.diary.domain.post.model.Post;
import com.diary.domain.post.model.dto.GetPostsResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.diary.domain.post.model.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    /*
    최신순, 오래된 순 페이징으로 Post 목록 조회
     */
    @Override
    public Page<Post> findAllWithPaging(Long memberId, String orderType, Pageable pageable) {
        //orderType이 oldest면 createdAt에 따라 내림차순으로 정렬
        //newest면 createdAt에 따라 오름차순으로 정렬
        OrderSpecifier<?> order = orderType.equals("oldest") ?
                post.createdAt.asc() : post.createdAt.desc();

        //해당 member의 post만 조회
        //pageable의 offset과 pageSize를 통해 한 페이지 조회
        List<Post> result = queryFactory
                .selectFrom(post)
                .where(post.member.id.eq(memberId))
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //총 post count 반환
        Long total = queryFactory
                .select(post.id.count())
                .from(post).
                where(post.member.id.eq(memberId))
                .fetchFirst();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public List<GetPostsResponse> findPostsByTagName(Member loginMember, String tagName, String orderType, Pageable pageable) {
        return queryFactory
                .select(Projections.fields(GetPostsResponse.class,
                        post.id.as("postId"),
                        post.title.as("title"),
                        post.beginAt.as("beginAt"),
                        post.finishAt.as("finishAt")
                ))
                .from(post)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

    }
}

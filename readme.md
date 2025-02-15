2020/2/3
<h1>대학생 위주 맛집 리뷰 어플</h1>
특징은 <br>1. 학생과 일반인의 권한이 다르다.<br>
2. 학생은 포토 리뷰, 평점, 좋아요 가능, 음식점에 모두가 볼 수 있는 좋아요 가능<br>
3. 일반인은 일반 리뷰, 좋아요 가능, 음식점에 개인만 보이는 좋아요 가능

<h2>핵심 기능은</h2>
1. 카테고리별로 음식점 분류해서 보기<br>
2. 포토 리뷰 올리기<br>

<h3>리뷰 Class</h3>
1. 아이디, 랭킹, 프사, 음식점 이름 (수정/삭제)<br>
2. 사진<br>
3. 좋아요, 날짜<br>
4. 본문(리뷰 내용)<br>
5. 댓글<br>
음식점 안에서 보여주는 리뷰 내용은 다름, 어댑터 클래스에서 처리함<br>

<p>
  나중에 게시물 오른쪽 위의 땡땡떙 버튼을 누르면 신고하기, 수정하기 등의 메뉴가 드롭다운 되게 만들 예정입니다.
</p>

리뷰 클래스 예시<br>
<div>
<img width="300" src="https://user-images.githubusercontent.com/59321616/73631197-af1cd900-469b-11ea-9e96-83c512415653.jpg">
</div>


<h2>데이터베이스/스토리지 구조</h2> <br>
1. 상위 폴더를 SKKU로 정했습니다 -> 나중에 다른 대학까지 확장성을 고려 <br>
2. DB에서 SKKU 폴더 밑에 노드를 Review와 Status로 나눴습니다. Review 밑에는 음식점 카테고리가 들어갈 것이고 <br>
각 카테고리 밑에는 음식점들이 들어갈 것입니다. 그리고 음식점 밑에 리뷰 노드들이 들어갈 것입니다. <br>
3. Status 노드 아래에는 실시간으로 올라오는 리뷰와 사람들의 글을 담을 것입니다. <br>
리뷰를 작성할 때 Status에도 반영하기 위해 음식점 리뷰를 작성하면 Review 노드를 업데이트 할 뿐만 아니라 <br>
Status 노드 또한 업데이트 하여야 합니다. (총 2번) <br>

<h4> https://console.firebase.google.com/project/reviewapp-80a16/database/reviewapp-80a16/data </h4> <br><br><br><br>

<h2>음식점에 구글맵 위치등록</h2>
https://support.google.com/maps/answer/18539?co=GENIE.Platform%3DDesktop&hl=ko&oco=1 을 참조하여, <br>
데이터베이스의 Restaurants/location에 "위도, 경도" 형식으로 넣어주시면 됩니다.

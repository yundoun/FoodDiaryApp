<!-- 첫번째 행: 2개 이미지 -->
<div style="display: flex; justify-content: space-between; margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/0d914b36-077f-4669-9ba7-91f92813d038" width="48%" alt="앱 스크린샷 1"/>
    <img src="https://github.com/user-attachments/assets/83a9af38-d9da-4af9-b50c-01f57c2e951a" width="48%" alt="앱 스크린샷 2"/>
</div>

<!-- 두번째 행: 3개 이미지 -->
<div style="display: flex; justify-content: space-between; margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/bed37d31-6677-493d-9819-6a679bf7f801" width="32%" alt="앱 스크린샷 3"/>
    <img src="https://github.com/user-attachments/assets/e86c2d0f-eb46-42cf-a223-ab674e585e28" width="32%" alt="앱 스크린샷 4"/>
    <img src="https://github.com/user-attachments/assets/7bdaa116-cc74-4f48-939e-a0cff26e521d" width="32%" alt="앱 스크린샷 5"/>
</div>

<!-- 세번째 행: 3개 이미지 -->
<div style="display: flex; justify-content: space-between;">
    <img src="https://github.com/user-attachments/assets/f92d293f-e8f7-425f-8c76-4db1ad503944" width="32%" alt="앱 스크린샷 6"/>
    <img src="https://github.com/user-attachments/assets/e25b44f2-2cc1-44c1-ad15-ff1c303c725d" width="32%" alt="앱 스크린샷 7"/>
    <img src="https://github.com/user-attachments/assets/f9a0764f-d722-4e17-9e9c-921e0b85dfbd" width="32%" alt="앱 스크린샷 8"/>
</div>


# 식단일지 애플리케이션 - FoodDiaryApp

## 프로젝트 개요
영양소 섭취 관리와 식단 기록을 돕는 안드로이드 애플리케이션입니다. 사용자가 일상적인 식사를 기록하고 영양소 섭취를 추적하여 건강한 식습관을 형성할 수 있도록 도와줍니다.

## 주요 기능
### 1. 식단 기록 및 관리
- 오픈 API 연동 : 외부 음식 데이터베이스 API를 활용하여 다양한 음식 정보를 제공
- 맞춤형 음식 추가 : 사용자가 직접 식품을 추가하여 데이터베이스를 확장 가능
- 식사 카테고리화 : 아침, 점심, 저녁, 간식으로 식사를 분류하여 체계적인 관리
- 중량 기반 계산 : 식품 섭취량을 정확하게 조절하여 섭취 영양소 맞춤화

### 2. 영양소 분석 및 시각화
- 실시간 영양소 계산
- 목표 섭취량 설정
- 시각적 표현 : 프로그래스바, 원형 차트 활용
- 하루 영양소 섭취 통계 제공

### 3. 사용자 경험 개선
- 캘린더를 사용하여 날짜별 관리
- 스와이프 & 드래그 : 삭제 및 순서 변경 등 상호작용 최적화
- 실행취소 : 사용자 실수에 대비한 식사 삭제 취소 기능

### 4. 부가기능
- 사진 기록
- 메모 기록
- 음성 검색
- 즐겨찾기 및 사용자 음식 추가

## 개발 과정 및 문제 해결
### 1. 데이터 모델 설계
데이터베이스 구조 설계 시 식품 정보, 사용자 식사 기록, 날짜별 메모 등 다양한 데이터 타입 간의 관계를 고려하였습니다.   
Room Database의 Entity, DAO, Repository 패턴을 활용하여 안정적인 데이터 아키텍처를 구축했습니다.

#### 주요 성과 :
- 6개의 엔티티 테이블 구현 : Food, Meal, DailyIntakeGoal, DailyIntakeRecord, Memo, Photo
- 데이터베이스 마이그레이션 처리 : Photo 테이블 추가
- Repository 패턴 적용 : FoodRepository, MealRepository 등 7개의 Repository 클래스를 통한 데이터 접근 계층화

### 2. 실시간 영양소 계산 구현
사용자가 식품을 추가하거나 섭취량을 변경할 때마다 실시간으로 영양소를 계산하여 UI에 반영하는 로직을 구현했습니다.   
LiveData와 ViewModel을 활용하여 데이터 변화를 관찰하고 자동으로 UI를 업데이트 합니다.

#### 주요 성과 :
- 4개의 주요 영양소(칼로리, 탄수화물, 단백질, 지방) 실시간 계산 및 표시
- MediatorLiveData 활용 : HomeViewModel에서의 20개 이상의 MediatorLiveData 객체를 통한 데이터 스트림 관리
- 식사별 계산 로직 : 아침, 점심, 저녁, 간식 각각에 대한 영양소 계산 및 누적 합계 산출

### 3. 날짜별 데이터 관리
캘린더에서 선택한 날짜에 맞는 식사 데이터와 메모를 로딩하는 과정에서 데이터 바인딩 이슈가 발생했었습니다.   
선택한 날짜를 ViewModel에서 관리하고, 해당 날짜 변경 시 관련 데이터를 새로 불러오는 방식으로 해결했습니다.

#### 주요 성과 : 
- 날짜 선택 기능 : 캘린더를 통한 직관적인 날짜 선택 및 데이터 로딩 구현
- 월별 데이터 렌더링 : 데이터를 미리 로드하여 캘린더 UI에 식사 기록 표시
- LiveData 연결 : LiveData를 활용한 UI 자동 업데이트

### 4. UX 최적화
스와이프 삭제, 드래그로 순서 변경 등 사용자 상호작용을 개선하는 과정에서 UI 반응성과 데이터 동기화 이슈가 있었습니다.   
ItemTouchHelper와 DiffUtil등을 활용하여 부드러운 사용자 경험을 제공하면서도 데이터 일관성을 유지했습니다.

#### 주요 성과 :
- ListAdapter 구현 : DiffUtil을 활용한 효율적인 RecyclerView 업데이트
- FastScrollRecyclerView 적용 : 섹션 인덱스를 통한 대량 음식 데이터 검색 기능 개선
- 제스처 인터페이스 : 스와이프 삭제 및 드래그 재정렬 기능 구현

## 기술 스택
### 아키텍처
- MVVM 패턴
- Repository 패턴

### 안드로이드 컴포넌트
- LiveData
- ViewBinding
- ViewModel
- Navigation Component

### 로컬 데이터베이스
- Room Database
- Entity & DAO

### UI 구성요소
- RecyclerView & ListAdapter
- ViewPager & TabLayout
- BottomSheet
- CardView

### 외부 라이브러리
- kizitonwose Calendar : 커스텀 캘린더
- Camera API : 사진 촬영

## 기술적 성과
### 1. MVVM 아키텍처 구현
- 데이터, UI, 비즈니스 로직을 분리한 구조화된 코드베이스 설계
- 2개의 핵심 ViewModel(HomeViewModel, DietViewModel) 구현
- ViewModelFactory를 통한 의존성 주입 패턴 적용

### 2. 데이터 바인딩 최적화
- 커스텀 바인딩 어댑터 구현으로 복잡한 데이터와 UI 연결을 단순화
- 실시간 영양소 계산 및 표시

### 3. 성능 최적화
- 비동기 데이터 처리를 위한 코루틴 활용 (viewModelScope)
- DiffUtil 기반 ListAdapter 활용으로 리스트 변경 시 필요한 부분만 업데이트
- Room Database 인덱싱 및 최적화된 쿼리 설계

### 4. 사용자 경험 개선
- 실행 취소 기능으로 사용자 실수 복구 가능
- 음식 검색, 정렬, 즐겨찾기 기능으로 빠른 데이터 접근성 제공
- 직관적인 사용자 인터페이스를 통한 식사 기록 관리 플로우 개선











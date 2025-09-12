package com.culturemate.culturemate_api.migration;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.repository.EventRepository;
import com.culturemate.culturemate_api.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RegionSnapshot 마이그레이션 컴포넌트
 * 
 * 기존 Event 데이터의 regionSnapshot 필드를 생성하여 성능 최적화 적용
 * 
 * 실행 방법:
 * - 자동 실행: 애플리케이션 시작 시 자동으로 실행됨
 * - 수동 실행: /api/v1/admin/migrate-region-snapshot 엔드포인트 호출
 * 
 * @author CultureMate Backend Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegionSnapshotMigration implements CommandLineRunner {

  private final EventRepository eventRepository;
  private final TogetherRepository togetherRepository;

  @Override
  public void run(String... args) throws Exception {
    // 개발/테스트 환경에서만 자동 실행
    // 운영 환경에서는 수동 실행 권장
    String activeProfile = System.getProperty("spring.profiles.active", "default");
    if (!"prod".equals(activeProfile)) {
      log.info("RegionSnapshot 마이그레이션을 시작합니다. (Profile: {}) - Event + Together", activeProfile);
      int eventMigrated = migrateEventSnapshots();
      int togetherMigrated = migrateTogetherSnapshots();
      log.info("총 마이그레이션 완료 - Event: {}, Together: {}", eventMigrated, togetherMigrated);
    } else {
      log.info("운영 환경에서는 RegionSnapshot 마이그레이션을 수동으로 실행해주세요.");
    }
  }

  /**
   * 기존 Event 데이터의 RegionSnapshot 생성
   * 
   * @return 마이그레이션된 이벤트 수
   */
  @Transactional
  public int migrateEventSnapshots() {
    log.info("=== RegionSnapshot 마이그레이션 시작 ===");

    // 1. regionSnapshot이 null인 이벤트 조회 (join fetch로 region 정보도 함께 조회)
    List<Event> eventsToMigrate = eventRepository.findEventsWithoutSnapshot();

    if (eventsToMigrate.isEmpty()) {
      log.info("마이그레이션할 이벤트가 없습니다. 모든 이벤트가 이미 스냅샷을 가지고 있습니다.");
      return 0;
    }

    log.info("마이그레이션 대상 이벤트 수: {}", eventsToMigrate.size());

    // 2. 각 이벤트의 regionSnapshot 생성
    int migratedCount = 0;
    int totalCount = eventsToMigrate.size();

    for (Event event : eventsToMigrate) {
      try {
        if (event.getRegion() != null) {
          // 스냅샷 생성 및 동기화
          event.updateRegionSnapshot(event.getRegion());
          migratedCount++;

          // 진행상황 로깅 (100개마다)
          if (migratedCount % 100 == 0) {
            log.info("마이그레이션 진행: {}/{} ({}%)", 
                    migratedCount, totalCount, 
                    Math.round((double) migratedCount / totalCount * 100));
          }
        } else {
          log.warn("Event ID {}는 region 정보가 없어 스킵합니다.", event.getId());
        }
      } catch (Exception e) {
        log.error("Event ID {} 마이그레이션 중 오류: {}", event.getId(), e.getMessage());
      }
    }

    // 3. 배치 저장 (성능 최적화)
    eventRepository.saveAll(eventsToMigrate);

    log.info("=== RegionSnapshot 마이그레이션 완료 ===");
    log.info("총 처리 이벤트: {}", totalCount);
    log.info("성공적으로 마이그레이션된 이벤트: {}", migratedCount);
    log.info("실패한 이벤트: {}", totalCount - migratedCount);

    return migratedCount;
  }

  /**
   * 기존 Together 데이터의 RegionSnapshot 생성
   * 
   * @return 마이그레이션된 Together 수
   */
  @Transactional
  public int migrateTogetherSnapshots() {
    log.info("=== Together RegionSnapshot 마이그레이션 시작 ===");

    // 1. regionSnapshot이 null인 Together 조회 (join fetch로 region 정보도 함께 조회)
    List<Together> togethersToMigrate = togetherRepository.findTogethersWithoutSnapshot();

    if (togethersToMigrate.isEmpty()) {
      log.info("마이그레이션할 Together가 없습니다. 모든 Together가 이미 스냅샷을 가지고 있습니다.");
      return 0;
    }

    log.info("마이그레이션 대상 Together 수: {}", togethersToMigrate.size());

    // 2. 각 Together의 regionSnapshot 생성
    int migratedCount = 0;
    int totalCount = togethersToMigrate.size();

    for (Together together : togethersToMigrate) {
      try {
        if (together.getRegion() != null) {
          // 스냅샷 생성 및 동기화
          together.updateRegionSnapshot(together.getRegion());
          migratedCount++;

          // 진행상황 로깅 (50개마다)
          if (migratedCount % 50 == 0) {
            log.info("Together 마이그레이션 진행: {}/{} ({}%)", 
                    migratedCount, totalCount, 
                    Math.round((double) migratedCount / totalCount * 100));
          }
        } else {
          log.warn("Together ID {}는 region 정보가 없어 스킵합니다.", together.getId());
        }
      } catch (Exception e) {
        log.error("Together ID {} 마이그레이션 중 오류: {}", together.getId(), e.getMessage());
      }
    }

    // 3. 배치 저장 (성능 최적화)
    togetherRepository.saveAll(togethersToMigrate);

    log.info("=== Together RegionSnapshot 마이그레이션 완료 ===");
    log.info("총 처리 Together: {}", totalCount);
    log.info("성공적으로 마이그레이션된 Together: {}", migratedCount);
    log.info("실패한 Together: {}", totalCount - migratedCount);

    return migratedCount;
  }

  /**
   * 마이그레이션 상태 확인
   * 
   * @return 마이그레이션 필요 여부
   */
  public boolean needsMigration() {
    long eventsWithoutSnapshot = eventRepository.countEventsWithoutSnapshot();
    log.info("마이그레이션이 필요한 이벤트 수: {}", eventsWithoutSnapshot);
    return eventsWithoutSnapshot > 0;
  }

  /**
   * 마이그레이션 통계 조회
   * 
   * @return 마이그레이션 통계 정보
   */
  public MigrationStats getMigrationStats() {
    long totalEvents = eventRepository.count();
    long eventsWithSnapshot = eventRepository.countEventsWithSnapshot();
    long eventsWithoutSnapshot = totalEvents - eventsWithSnapshot;

    return MigrationStats.builder()
        .totalEvents(totalEvents)
        .eventsWithSnapshot(eventsWithSnapshot)
        .eventsWithoutSnapshot(eventsWithoutSnapshot)
        .migrationProgress(totalEvents > 0 ? (double) eventsWithSnapshot / totalEvents * 100 : 0)
        .build();
  }

  /**
   * 마이그레이션 통계 정보
   */
  public static class MigrationStats {
    private final long totalEvents;
    private final long eventsWithSnapshot;
    private final long eventsWithoutSnapshot;
    private final double migrationProgress;

    private MigrationStats(long totalEvents, long eventsWithSnapshot, 
                          long eventsWithoutSnapshot, double migrationProgress) {
      this.totalEvents = totalEvents;
      this.eventsWithSnapshot = eventsWithSnapshot;
      this.eventsWithoutSnapshot = eventsWithoutSnapshot;
      this.migrationProgress = migrationProgress;
    }

    public static Builder builder() {
      return new Builder();
    }

    // Getters
    public long getTotalEvents() { return totalEvents; }
    public long getEventsWithSnapshot() { return eventsWithSnapshot; }
    public long getEventsWithoutSnapshot() { return eventsWithoutSnapshot; }
    public double getMigrationProgress() { return migrationProgress; }

    public static class Builder {
      private long totalEvents;
      private long eventsWithSnapshot;
      private long eventsWithoutSnapshot;
      private double migrationProgress;

      public Builder totalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
        return this;
      }

      public Builder eventsWithSnapshot(long eventsWithSnapshot) {
        this.eventsWithSnapshot = eventsWithSnapshot;
        return this;
      }

      public Builder eventsWithoutSnapshot(long eventsWithoutSnapshot) {
        this.eventsWithoutSnapshot = eventsWithoutSnapshot;
        return this;
      }

      public Builder migrationProgress(double migrationProgress) {
        this.migrationProgress = migrationProgress;
        return this;
      }

      public MigrationStats build() {
        return new MigrationStats(totalEvents, eventsWithSnapshot, eventsWithoutSnapshot, migrationProgress);
      }
    }

    @Override
    public String toString() {
      return String.format("MigrationStats{totalEvents=%d, eventsWithSnapshot=%d, eventsWithoutSnapshot=%d, migrationProgress=%.2f%%}",
          totalEvents, eventsWithSnapshot, eventsWithoutSnapshot, migrationProgress);
    }
  }
}
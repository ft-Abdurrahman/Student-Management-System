package com.sms.fee.repository;
import com.sms.fee.entity.FeeReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> { List<FeeReceipt> findByStudentIdOrderByPaidAtDesc(Long studentId); }
